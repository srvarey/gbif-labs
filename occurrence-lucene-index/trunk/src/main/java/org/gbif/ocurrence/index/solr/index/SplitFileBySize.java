package org.gbif.ocurrence.index.solr.index;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class SplitFileBySize {

  private static final Character lineBreak = new Character('\n');

  private static String[] fileNames = new String[]{
      "/indexinput/attempt_201106170746_0763_r_000000_00", "/indexinput/attempt_201106170746_0763_r_000000_01",
      "/indexinput/attempt_201106170746_0763_r_000000_02", "/indexinput/attempt_201106170746_0763_r_000000_03"};

  public static void main(String[] args) {
    try {
      LinkedHashMap<Long, Long> readers = SolrMultiThreadIndexingJob.getReaders(args[0], Integer.parseInt(args[1]));
      System.out.println(readers.toString());
      File file = new File(args[0]);
      FileInputStream fileInputStreamMain = new FileInputStream(file);
      for (Long key : readers.keySet()) {
        fileInputStreamMain.close();
        fileInputStreamMain = new FileInputStream(file);
        fileInputStreamMain.skip(readers.get(key));
        System.out.println(((char) fileInputStreamMain.read()) == lineBreak);
      }
      LinkedHashMap<String, Integer> distribution = SolrMultiThreadIndexingJob.getThreadsPerFile(
          Arrays.asList(fileNames), 500);
      System.out.println(distribution.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
