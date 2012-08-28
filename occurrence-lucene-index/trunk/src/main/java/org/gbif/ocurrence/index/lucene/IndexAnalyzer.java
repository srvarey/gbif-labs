package org.gbif.ocurrence.index.lucene;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: fede
 * Date: 6/28/11
 * Time: 4:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class IndexAnalyzer {

  public static long analyzeIndexDir(String dirName) {
    File childFile = new File(dirName);
    if (childFile.isDirectory()) {
      Directory directory = null;
      IndexReader indexReader = null;
      try {
        System.out.println("Index: " + childFile.getName());
        directory = FSDirectory.open(childFile);
        indexReader = IndexReader.open(directory);
        System.out.println("Field: ");
        Collection<String> fieldNames = indexReader.getFieldNames(IndexReader.FieldOption.ALL);
        for (String fieldName : fieldNames) {
          System.out.println(fieldName);
        }
        System.out.println("# of docs:" + indexReader.numDocs());
        System.out.println("---------------------------------------------------");
        return indexReader.numDocs();
      } catch (Exception e) {
        System.out.println("ERROR analyzing index: " + dirName + "->" + e);
      } finally {
        try {
          if (indexReader != null) {
            indexReader.close();
          }
          if (directory != null) {
            directory.close();
          }
        } catch (Exception e) {
        }
      }
    }
    return 0L;
  }

  public static void analyzeIndexRootDir(String rootDir) {
    File fileRootDir = new File(rootDir);
    Long totalDocs = 0l;
    if (fileRootDir.isDirectory()) {
      String[] children = fileRootDir.list();
      for (String child : children) {
        totalDocs += analyzeIndexDir(rootDir + child);
      }
    }
    System.out.print("Total docs:" + totalDocs);
  }

  public static void main(String[] args) throws IOException {
    analyzeIndexDir("//indexmerged//");
  }
}
