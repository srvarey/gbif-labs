package org.gbif.common.messaging;

import org.gbif.api.vocabulary.OccurrenceSchemaType;
import org.gbif.common.messaging.api.OccurrenceFragmentedMessage;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FileWritingListenerTest {

  private static final String PATH = "/tmp";

  @Test
  public void testWriting() {
    FileWritingListener<OccurrenceFragmentedMessage> listener =
      new FileWritingListener<OccurrenceFragmentedMessage>(PATH);
    UUID datasetKey = UUID.randomUUID();
    System.out.println("Writing to dataset [" + datasetKey + "]");
    OccurrenceFragmentedMessage msg =
      new OccurrenceFragmentedMessage(datasetKey, 1, "bytes".getBytes(), OccurrenceSchemaType.DWC_1_4, null);

    listener.handleMessage(msg);

    String fullPath = PATH + File.separator + datasetKey;
    List<String> fileNames = FileUtils.getFilenames(fullPath);
    assertEquals(1, fileNames.size());
    for (String fileName : fileNames) {
      System.out.println("Got file [" + fileName + "]");
    }

    FileUtils.deleteFiles(fullPath);
  }
}
