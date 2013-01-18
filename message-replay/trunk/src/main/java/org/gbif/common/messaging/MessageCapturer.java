package org.gbif.common.messaging;

import org.gbif.common.messaging.api.MessageCallback;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Captures DatasetBasedMessages of a specific type, on a specified queue, from RabbitMQ, and writes them to disk at a
 * specified path.
 */
public class MessageCapturer {

  private MessagingService messagingService;

  /**
   * Build the object with the rabbitMQ parameters. Call {@link #shutdown()} when finished to release resources.
   *
   * @param threadcount the number of threads with which to pull messages off the queue
   *
   * @throws IOException if connection fails
   */
  public MessageCapturer(String username, String password, String virtualhost, String hostname, int port,
    int threadcount) throws IOException {
    ConnectionFactory factory = new ConnectionFactory();

    factory.setUsername(username);
    factory.setPassword(password);
    factory.setVirtualHost(virtualhost);
    factory.setHost(hostname);
    factory.setPort(port);
    Connection connection = factory.newConnection();

    if (threadcount > 1) {
      ExecutorService tp = Executors.newFixedThreadPool(threadcount);
      messagingService = new MessagingService(connection, tp);
    } else {
      messagingService = new MessagingService(connection);
    }
    messagingService.declareAllExchangesFromRegistry();
  }

  /**
   * Begin capturing messages of the given type (must implement DatasetBasedMessage) using the given queue name.
   * Messages will be written to the given path, in directories for each dataset key.
   *
   * @param messageType the class of message to capture
   * @param queue       name of the queue to create
   * @param path        local directory to write files to
   *
   * @throws IOException            if there are errors reading or writing msgs
   * @throws ClassNotFoundException if the messageType is not found
   */
  public void capture(String messageType, String queue, String path) throws IOException, ClassNotFoundException {
    Class messageClass = Class.forName(messageType);
    MessageCallback callback = new FileWritingListener(path);
    messagingService.listen(messageClass, queue, callback);
  }


  /**
   * Close the messagingService, releasing resources.
   */
  public void shutdown() {
    if (messagingService != null) {
      messagingService.close();
    }
  }

  public static void main(String[] args) throws IOException, ClassNotFoundException {
    checkArgument(args.length > 0,
      "Usage MessageCapturer <path for writing> <queue to listen> <message type to listen for>");
    checkNotNull(args[0], "first argument must be the path for writing messages");
    checkNotNull(args[1], "second argument must be the queue to listen on");
    checkNotNull(args[2], "third argument must be the message type to listen for");

    InputStream in = MessageCapturer.class.getClassLoader().getResourceAsStream("application.properties");
    Properties props = new Properties();
    props.load(in);
    in.close();

    System.out.println("Using properties:");
    for (String key : props.stringPropertyNames()) {
      System.out.println("key [" + key + "] val [" + props.get(key) + "]");
    }

    MessageCapturer instance = new MessageCapturer((String) props.get("username"), (String) props.get("password"),
      (String) props.get("virtualhost"), (String) props.get("hostname"), Integer.valueOf((String) props.get("port")),
      Integer.valueOf((String) props.get("threadcount")));

    instance.capture(args[2], args[1], args[0]);

    System.out.println("Hit any key + enter to quit.");
    Scanner input = new Scanner(System.in);
    System.out.println("Caught " + input.next() + ", quitting.");
    instance.shutdown();
  }
}
