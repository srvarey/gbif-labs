package org.gbif.common.messaging;

import java.io.File;
import java.util.List;

import com.google.common.collect.Lists;

public class FileUtils {
  public static final String PATH = "/tmp";

  public static List<String> getFilenames(String path) {
    System.out.println("Getting file list for path [" + path + "]");
    File dir = new File(path);
    File[] files = dir.listFiles();
    List<String> fileNames = Lists.newArrayList();
    for (File file : files) {
      System.out.println("Found file [" + file.getName() + "]");
      fileNames.add(file.getName());
    }
    return fileNames;
  }

  public static void deleteFiles(String path) {
    File dir = new File(path);
    for (File file : dir.listFiles()) {
      file.delete();
    }
    dir.delete();
  }
}
