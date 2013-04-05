package org.gbif.common.messaging;

import org.gbif.common.messaging.api.DatasetBasedMessage;
import org.gbif.common.messaging.api.MessageCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A message listener for DatasetBasedMessages that serializes the incoming message and writes it to the specified
 * directory. Note that the order of incoming messages is not preserved for future replays.
 *
 * @param <T> the message type to listen for
 */
public class FileWritingListener<T extends DatasetBasedMessage> implements MessageCallback<T> {

  private static final Logger LOG = LoggerFactory.getLogger(MessageCapturer.class);

  private final String writePath;
  private final ObjectMapper mapper = new ObjectMapper();

  public FileWritingListener(String writePath) {
    this.writePath = writePath;
  }

  @Override
  public void handleMessage(T message) {
    String datasetKey = message.getDatasetUuid().toString();
    LOG.debug("Got message for dataset [{}]", datasetKey);
    FileOutputStream fos = null;
    try {
      byte[] rawMsg = mapper.writeValueAsBytes(message);
      File datasetDir = new File(writePath + File.separator + datasetKey);
      if (!datasetDir.isDirectory() && !datasetDir.mkdirs()) {
        LOG.error("Could not make dirs for [{}], aborting.", datasetDir.getName());
        return;
      }
      String fileName = message.getClass().getName() + "-" + UUID.randomUUID();
      fos = new FileOutputStream(new File(datasetDir, fileName));
      fos.write(rawMsg);
    } catch (FileNotFoundException e) {
      LOG.warn("Couldn't write file", e);
    } catch (IOException e) {
      LOG.warn("Couldn't serialize message", e);
    } finally {
      if (fos != null) {
        try {
          fos.close();
        } catch (IOException e) {
          LOG.debug("Couldn't close file, ignoring", e);
        }
      }
    }

  }
}
