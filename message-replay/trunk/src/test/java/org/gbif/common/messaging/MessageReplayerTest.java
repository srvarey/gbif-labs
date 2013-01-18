package org.gbif.common.messaging;

import org.gbif.api.vocabulary.OccurrenceSchemaType;
import org.gbif.common.messaging.api.OccurrenceFragmentedMessage;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class MessageReplayerTest {

  @Test
  public void testDeserialization() throws IOException, ClassNotFoundException {
    FileWritingListener<OccurrenceFragmentedMessage> listener =
      new FileWritingListener<OccurrenceFragmentedMessage>(FileUtils.PATH);
    UUID datasetKey = UUID.randomUUID();
    System.out.println("Writing to dataset [" + datasetKey + "]");
    OccurrenceFragmentedMessage msg =
      new OccurrenceFragmentedMessage(datasetKey, 1, "bytes".getBytes(), OccurrenceSchemaType.DWC_1_4);

    listener.handleMessage(msg);

    String fullPath = FileUtils.PATH + File.separator + datasetKey;
    List<String> fileNames = FileUtils.getFilenames(fullPath);

    MessageReplayer replayer = new MessageReplayer();
    OccurrenceFragmentedMessage readMsg =
      (OccurrenceFragmentedMessage) replayer.readMessageFromFile(new File(fullPath, fileNames.get(0)), OccurrenceFragmentedMessage.class.getName());
    assertEquals(msg.getDatasetUuid(), readMsg.getDatasetUuid());
    assertTrue(Arrays.equals(msg.getFragment(), readMsg.getFragment()));

    FileUtils.deleteFiles(fullPath);
  }
}
