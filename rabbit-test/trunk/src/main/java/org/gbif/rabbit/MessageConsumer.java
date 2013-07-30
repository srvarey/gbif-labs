package org.gbif.rabbit;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This is the consumer used by {@link MessagingService} to handle AMQP deliveries. It deserializes the content and
 * forwards it to a callback. All messages are automatically either being rejected or acked. See Javadoc for
 * {@link MessagingService#listen(Class, String, String, String, MessageCallback)} for details.
 * <p/>
 * This class is thread-safe but stateful.
 * 
 * @param <T> type of message to handle, we'll try to deserialize the received content in an instance of this type
 */
class MessageConsumer extends DefaultConsumer {

  private static final Logger LOG = LoggerFactory.getLogger(MessageConsumer.class);

  private final ExecutorService executorService;
  private final Callback callback;

  /**
   * Constructs a new instance and records its association to the passed-in channel.
   * 
   * @param channel the channel to which this consumer is attached
   */
  MessageConsumer(
    Channel channel, ExecutorService executorService, Callback callback) {
    super(channel);
    checkNotNull(channel, "channel can't be null");
    this.callback = callback;

    this.executorService = checkNotNull(executorService, "executorService can't be null");
  }

  /**
   * Delivery is being handled by deserializing the message body and creating a Runnable that's being put on the
   * ExecutorService.
   * <p/>
   * If deserializing fails for any reason we reject the message and don't requeue it, apart from that we ack the
   * message no matter the outcome of the callback.
   * <p/>
   * If submitting a job to the ExecutorService fails we send a rejection back to RabbitMQ.
   */
  @Override
  public void handleDelivery(
    final String consumerTag, final Envelope envelope, AMQP.BasicProperties properties, byte[] body
    ) throws IOException {
    LOG.debug("Handling delivery: [{}]", envelope.getDeliveryTag());

    String message = new String(body);

    try {
      executorService.submit(new AckingRunnable(message, callback, envelope.getDeliveryTag()));
    } catch (RejectedExecutionException ignored) {
      LOG.info("Could not submit job to Executor service for message with delivery key [{}]",
        envelope.getDeliveryTag());
      getChannel().basicReject(envelope.getDeliveryTag(), false);
    }
  }

  /**
   * The runnable that's being submitted to the ExecutorService. All it does is wrap the callback and acknowledges all
   * messages it handles.
   */
  class AckingRunnable implements Runnable {

    private final String message;
    private final long deliveryTag;
    private final Callback callback;

    AckingRunnable(String message, Callback callback, long deliveryTag) {
      this.message = message;
      this.deliveryTag = deliveryTag;
      this.callback = callback;
    }

    @Override
    public void run() {
      try {
        callback.handleMessage(message);
      } catch (Exception e) {
        LOG.warn("Error handling message, will be acknowledged anyway and not retried", e);
      } finally {
        try {
          getChannel().basicAck(deliveryTag, false);
        } catch (IOException e) {
          LOG.warn("Failure acknowledging message [{}] of type [{}]",
            deliveryTag,
            message);
        }
      }
    }

  }
}