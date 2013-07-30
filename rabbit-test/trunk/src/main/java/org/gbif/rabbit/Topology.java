package org.gbif.rabbit;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.RateLimiter;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runs a topology on rabbit with 5 steps simulating what the GBIF indexing is doing. The 5 simulated steps are:
 * a) Fragmenter - a rapid message emmitter
 * b) Persister - a rapid consumer, emitting a message per persist
 * c) Extractor - a rapid consumer, emitting a message per consumption
 * d) Interpreter - a rate limited consumer, emitting a message per consumption
 * e) Mapper - a consumer that runs in bursts, with intervals in the burst
 * nohup java -Xmx256M -cp topology.jar org.gbif.rabbit.Topology mq.gbif.org tim tim /users/trobertson 1000 10 20000 10
 * 20000 10 4000 10000 1 23 false &
 */
public class Topology {

  private static final Logger LOG = LoggerFactory.getLogger(Topology.class);

  private static final String EXCHANGE = "occurrence";
  private static final String FRAG_QUEUE = "1_fragments";
  private static final String PERSIST_QUEUE = "2_persists";
  private static final String EXTRACT_QUEUE = "3_extracts";
  private static final String MAP_QUEUE = "4_maps_zoom";
  private static final String FRAG_ROUTING = "fragments.q";
  private static final String PERSIST_ROUTING = "persists.q";
  private static final String EXTRACT_ROUTING = "extracts.q";
  private static final String INTERPRET_ROUTING = "interprets.q";

  // TODO: did I read this was bad?
  private static final int PREFETCH_COUNT = 100;

  private final ConnectionFactory factory;
  private final RateLimiter fragmenterLimiter;
  private final RateLimiter persisterLimiter; // total across threads
  private final RateLimiter extracterLimiter;
  private final RateLimiter interpretLimiter;
  private final int mapperBatchSize;
  private final int mapperPauseSecs;
  private final ExecutorService fragmenterService = Executors.newFixedThreadPool(1);
  private final ExecutorService persisterService;
  private final ExecutorService extractorService;
  private final ExecutorService interpreterService;
  private final int numberMappers;
  private final boolean reuseConnections;

  public Topology(ConnectionFactory factory, RateLimiter fragmenterLimiter, RateLimiter persisterLimiter,
    RateLimiter extracterLimiter, RateLimiter interpretLimiter, int mapperBatchSize, int mapperPauseSecs,
    ExecutorService persisterService, ExecutorService extractorService, ExecutorService interpreterService,
    int numberMappers, boolean reuseConnections) {
    super();
    this.factory = factory;
    this.fragmenterLimiter = fragmenterLimiter;
    this.persisterLimiter = persisterLimiter;
    this.extracterLimiter = extracterLimiter;
    this.interpretLimiter = interpretLimiter;
    this.mapperBatchSize = mapperBatchSize;
    this.mapperPauseSecs = mapperPauseSecs;
    this.persisterService = persisterService;
    this.extractorService = extractorService;
    this.interpreterService = interpreterService;
    this.numberMappers = numberMappers;
    this.reuseConnections = reuseConnections;
  }

  private void run() throws IOException {

    // fragment emitter (sends to frag q)
    Connection connection = factory.newConnection();
    Sender fragmenter = new Sender(fragmenterLimiter, connection, FRAG_ROUTING);
    fragmenterService.submit(fragmenter);

    // persister (listens to fragments, then send to persist q)
    connection = factory.newConnection();
    Sender sender = new Sender(persisterLimiter, connection, PERSIST_ROUTING);
    connection = reuseConnections ? connection : factory.newConnection();
    Listener listener =
      new Listener(connection, FRAG_QUEUE, FRAG_ROUTING, EXCHANGE, persisterService,
        new SendingCallback(sender, "persisted:"));
    listener.run();

    // extractor (listens to persists, then sends to extracts q)
    connection = factory.newConnection();
    sender = new Sender(extracterLimiter, connection, EXTRACT_ROUTING);
    connection = reuseConnections ? connection : factory.newConnection();
    listener =
      new Listener(connection, PERSIST_QUEUE, PERSIST_ROUTING, EXCHANGE, extractorService,
        new SendingCallback(sender, "extracted:"));
    listener.run();

    // interpreter (listens to extracts, then sends to interpret q)
    connection = factory.newConnection();
    sender = new Sender(interpretLimiter, connection, INTERPRET_ROUTING);
    connection = reuseConnections ? connection : factory.newConnection();
    listener =
      new Listener(connection, EXTRACT_QUEUE, EXTRACT_ROUTING, EXCHANGE, interpreterService,
        new SendingCallback(sender, "interpreted:"));
    listener.run();

    // listen to the interpretters with some fake mappers
    for (int i = 0; i < numberMappers; i++) {
      connection = factory.newConnection();
      listener =
        new Listener(connection, MAP_QUEUE + "_" + i, INTERPRET_ROUTING, EXCHANGE,
          Executors.newFixedThreadPool(1),
          new MapperCallback(mapperBatchSize, mapperPauseSecs));
      listener.run();
    }
  }

  private static class Listener implements Runnable {

    private final Connection connection;
    private final String queueName;
    private final String routingKey;
    private final String exchange;
    private final ExecutorService executorService;
    private final Callback callback;

    public Listener(Connection connection, String queueName, String routingKey, String exchange,
      ExecutorService executorService, Callback callback) {
      super();
      this.connection = connection;
      this.queueName = queueName;
      this.routingKey = routingKey;
      this.exchange = exchange;
      this.executorService = executorService;
      this.callback = callback;
    }


    @Override
    public void run() {
      try {
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(exchange, "topic", true);
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, exchange, routingKey);
        channel.basicQos(PREFETCH_COUNT);
        channel.basicConsume(queueName, new MessageConsumer(channel, executorService, callback));
      } catch (IOException e) {
        LOG.error("Error receiving: {}", e.getMessage(), e);
      }

    }
  }

  private static class LogCallback implements Callback {

    @Override
    public void handleMessage(String message) {
      LOG.info(message);
    }
  }

  private static class MapperCallback implements Callback {

    private final int mapperBatchSize;
    private final int mapperPauseSecs;
    private int messagesRead = 0;

    public MapperCallback(int mapperBatchSize, int mapperPauseSecs) {

      this.mapperBatchSize = mapperBatchSize;
      this.mapperPauseSecs = mapperPauseSecs;
    }


    @Override
    public void handleMessage(String message) {
      messagesRead++;
      if (messagesRead > mapperBatchSize) {
        messagesRead = 0;
        LOG.info("Flushing map of batch size[{}] and simulating a pause of [{}]", mapperBatchSize, mapperPauseSecs);
        try {
          Thread.sleep(mapperPauseSecs * 1000);
        } catch (InterruptedException e) {
        }
      }
    }
  }


  private static class SendingCallback implements Callback {

    private final Sender sender;
    private final String prefix;

    @Override
    public void handleMessage(String message) {
      try {
        sender.send(prefix + message);
      } catch (IOException e) {
        LOG.error("Error sending: {}", e.getMessage(), e);
      }
    }

    public SendingCallback(Sender sender, String prefix) {
      this.sender = sender;
      this.prefix = prefix;
    }
  }

  private static class Sender implements Runnable {

    private final RateLimiter limit;
    private final Connection connection;
    private final String routingKey;
    // reuse channels for sending
    private final ConcurrentLinkedQueue<Channel> channelPool = Queues.newConcurrentLinkedQueue();

    public Sender(RateLimiter limit, Connection connection, String routingKey) {
      super();
      this.limit = limit;
      this.connection = connection;
      this.routingKey = routingKey;
    }

    @Override
    public void run() {
      try {
        while (true) {
          send("A dummy message");
        }
      } catch (IOException e) {
        LOG.error("Error sending: {}", e.getMessage(), e);
      }
    }

    public void send(String message) throws IOException {
      limit.acquire();
      Channel channel = provideChannel();
      try {
        // channel.basicPublish(EXCHANGE, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        channel.basicPublish(EXCHANGE, routingKey, MessageProperties.TEXT_PLAIN, message.getBytes());
        LOG.info("sent[{}] to routing[{}]", message, routingKey);
      } finally {
        releaseChannel(channel);
      }
    }

    private Channel provideChannel() throws IOException {
      Channel channel = channelPool.poll();
      if (channel == null) { // none for reuse
        LOG.debug("No pooled channels available, creating a new one");
        channel = connection.createChannel();
      }
      return channel;
    }

    private void releaseChannel(Channel channel) {
      channelPool.add(channel);
      LOG.debug("Channel returned to the pool. Available channels for reuse: {}", channelPool.size());
    }
  }

  public static void main(String[] args) {
    if (args.length != 15) {
      printUsage();
      return;
    }
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(args[0]);
    factory.setUsername(args[1]);
    factory.setPassword(args[2]);
    factory.setVirtualHost(args[3]);

    Topology app = new Topology(
      factory,
      RateLimiter.create(Double.parseDouble(args[4])),
      RateLimiter.create(Double.parseDouble(args[6])),
      RateLimiter.create(Double.parseDouble(args[8])),
      RateLimiter.create(Double.parseDouble(args[10])),
      Integer.parseInt(args[11]),
      Integer.parseInt(args[12]),
      Executors.newFixedThreadPool(Integer.parseInt(args[5])),
      Executors.newFixedThreadPool(Integer.parseInt(args[7])),
      Executors.newFixedThreadPool(Integer.parseInt(args[9])),
      Integer.parseInt(args[13]),
      Boolean.parseBoolean(args[14]));

    try {
      app.run();
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  private static void printUsage() {
    System.out.println("Args[0] should be the rabbit server");
    System.out.println("Args[1] should be the rabbit VH");
    System.out.println("Args[2] should be the rabbit username");
    System.out.println("Args[3] should be the rabbit password");
    System.out.println("Args[4] should be the fragmenter rate limit per second");
    System.out.println("Args[5] should be the number of persisters");
    System.out.println("Args[6] should be the persister rate limit per second");
    System.out.println("Args[7] should be the number of extractors");
    System.out.println("Args[8] should be the extractor rate limit per second");
    System.out.println("Args[9] should be the number of interpreters");
    System.out.println("Args[10] should be the interpreters rate limit per second");
    System.out.println("Args[11] should be the mappers messages consumed before a pause");
    System.out.println("Args[12] should be the mappers pause inteval in seconds");
    System.out.println("Args[13] should be the number of mappers");


    System.out.println("Args[14] should be whether connections are reused");
  }
}
