package org.gbif.common.messaging;

import org.gbif.common.messaging.api.DatasetBasedMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Reads serialized messages from disk and sends them on the specified queue.
 */
public class MessageReplayer {

  private static final Logger LOG = LoggerFactory.getLogger(MessageReplayer.class);

  private static final String USERNAME = "guest";
  private static final String PASSWORD = "guest";
  private static final String VIRTUALHOST = "/";
  private static final String HOSTNAME = "localhost";
  private static final int PORT = 5672;
  private static final int THREADCOUNT = 10;
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final MessagingService messagingService;

  public MessageReplayer() throws IOException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setUsername(USERNAME);
    factory.setPassword(PASSWORD);
    factory.setVirtualHost(VIRTUALHOST);
    factory.setHost(HOSTNAME);
    factory.setPort(PORT);
    Connection connection = factory.newConnection();
    ExecutorService tp = Executors.newFixedThreadPool(THREADCOUNT);
    messagingService = new MessagingService(connection, tp);
    messagingService.declareAllExchangesFromRegistry();
  }

  /**
   * Will replay messages found by searching the given filePath and one level below. If a datasetUuid is specified,
   * only messages found for that dataset will be sent. If a msgClazz is specified only messages of those type will be
   * sent.
   *
   * @param filePath    the file path to search for messages
   * @param datasetUuid send messages for this dataset only - leave null for all datasets
   * @param msgClazz    send messages of this type only - leave null for all types
   */
  public void replayMessages(String filePath, @Nullable String datasetUuid,
    @Nullable final Class<?> msgClazz) throws IOException, ClassNotFoundException {
    File parentDir = new File(filePath);

    // get list of dirs to search
    File[] dirsToSearch;
    if (datasetUuid == null) {
      dirsToSearch = parentDir.listFiles();
    } else {
      dirsToSearch = new File[1];
      dirsToSearch[0] = new File(parentDir, datasetUuid);
    }

    if (dirsToSearch == null) {
      LOG.warn("Could not find any directories to search, exiting");
      return;
    }

    // either accept all files or just those for the given msgClazz
    FilenameFilter filter;
    if (msgClazz == null) {
      filter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          return true;
        }
      };
    } else {
      filter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          return name.startsWith(msgClazz.getName());
        }
      };
    }

    for (File dir : dirsToSearch) {
      String[] fileNames = dir.list(filter);
      for (String fileName : fileNames) {
        String className = msgClazz == null ? fileName.substring(0, fileName.indexOf('-')) : msgClazz.getName();
        DatasetBasedMessage msg = readMessageFromFile(new File(dir, fileName), className);
        if (msg != null) {
          messagingService.send(msg);
        }
      }
    }
  }

  @Nullable
  DatasetBasedMessage readMessageFromFile(File file, String className) throws ClassNotFoundException, IOException {
    LOG.debug("reading message file from [{}]", file.getAbsolutePath());
    Class<?> clazz = Class.forName(className);
    DatasetBasedMessage msg = null;
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(file);
      byte[] rawMsg = new byte[(int) file.length()];
      fis.read(rawMsg);
      msg = (DatasetBasedMessage) MAPPER.readValue(rawMsg, clazz);
    } finally {
      if (fis != null) {
        fis.close();
      }
    }

    return msg;
  }

  /**
   * Close the messagingService, releasing resources.
   */
  public void shutdown() {
    messagingService.close();
  }

  /**
   * Arguments should be:
   * 0: path to directory that contains the directories for each dataset key
   * 1: dataset uuid to replay, or "null" for all datasets
   * 2: message class to replay (e.g. "org.gbif.common.messaging.api.OccurrenceFragmentedMessage") or "null" for all
   *
   * @throws IOException            if reading or sending messages fails
   * @throws ClassNotFoundException if the given message type is unknown
   */
  public static void main(String[] args) throws IOException, ClassNotFoundException {
    checkArgument(args.length > 0,
      "Usage: MessageReplayer <path to files> <dataset uuid or 'null'> <msg class or 'null'>");
    checkNotNull(args[0], "first argument must be path containing message files");

    // need directory to read messages
    String path = args[0];

    // need dataset uuid to replay, or null for all
    String datasetKey = args[1].equals("null") ? null : args[1];

    // need message type to replay, or null for all
    String msgType = args[2].equals("null") ? null : args[2];
    Class<?> msgClass = msgType == null ? null : Class.forName(msgType);

    MessageReplayer instance = new MessageReplayer();

    instance.replayMessages(path, datasetKey, msgClass);
    instance.shutdown();
  }
}
