package org.gbif.ocurrence.index.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: fede
 * Date: 6/29/11
 * Time: 3:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class IndexMerger {
  /** Index all text files under a directory. */
  public static void main(String[] args) {

    if (args.length != 2) {
      System.out.println("Usage: java -jar IndexMerger.jar " + "existing_indexes_dir merged_index_dir");
      System.out.println(" existing_indexes_dir: A directory where the " + "indexes that have to merged exist");
      System.out.println("   e.g. indexes/");
      System.out.println("   e.g.         index1");
      System.out.println("   e.g.         index2");
      System.out.println("   e.g.         index3");
      System.out.println(" merged_index_dir: A directory where the merged " + "index will be stored");
      System.out.println("   e.g. merged_indexes");
      System.exit(1);
    }
    String[] inputDirs = args[0].split(",");
    mergeIndexes(args[1], inputDirs);
  }

  public static void mergeIndexes(String resultDir, String... inputDirs) {
    File[] INDEXES_DIR = new File[inputDirs.length];
    for (int i = 0; i < inputDirs.length; i++) {
      INDEXES_DIR[i] = new File(inputDirs[i]);
    }
    File INDEX_DIR = new File(resultDir);

    INDEX_DIR.mkdir();

    Date start = new Date();

    try {
      IndexWriter writer = new IndexWriter(FSDirectory.open(INDEX_DIR), new StandardAnalyzer(Version.LUCENE_CURRENT),
          true, IndexWriter.MaxFieldLength.UNLIMITED);
      writer.setMergeFactor(1000);
      writer.setRAMBufferSizeMB(50);

      Directory indexes[] = new Directory[INDEXES_DIR.length];

      for (int i = 0; i < INDEXES_DIR.length; i++) {
        System.out.println("Adding: " + INDEXES_DIR[i].getName());
        String dirIndexName = INDEXES_DIR[i].getAbsolutePath();
        indexes[i] = FSDirectory.open(new File(dirIndexName));
      }

      System.out.print("Merging added indexes...");
      writer.addIndexesNoOptimize(indexes);
      System.out.println("done");

      System.out.print("Optimizing index...");
      writer.optimize();
      writer.close();
      System.out.println("done");

      Date end = new Date();
      System.out.println("It took: " + ((end.getTime() - start.getTime()) / 1000) + "\"");

    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
