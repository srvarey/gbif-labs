package org.gbif.hbase.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Looks through the raw_occurrence_record table in mysql and emits every nth id.  Those ids are then intended
 * to be used as split points for pre-splitting an hbase table.  The file that this class generates can NOT be used
 * in the hbase table definition (in the shell) directly, because the ints will be interpreted as strings. Another
 * class needs to read the file, translate the ints to bytes and create the table programatically.
 *
 * @see TableCreator
 */
public class RorSplitCounter {

  private static Logger log = LoggerFactory.getLogger(RorSplitCounter.class);

  private void generateSplits(String rorTable, int splitSize, String fileName) {
    Connection con = null;
    try {
      File outFile = new File(fileName);
      outFile.delete();
      outFile.createNewFile();
      FileOutputStream fos = new FileOutputStream(outFile);
      OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8");

      con = DriverManager.getConnection("jdbc:mysql://mogo.gbif.org:3306/" + rorTable, "hbase", "hbasepassword");
      Statement stmt = con.createStatement();

      ResultSet rs = stmt.executeQuery("SELECT MAX(id) as max_id FROM raw_occurrence_record");
      rs.next();
      int maxId = rs.getInt("max_id");
      log.info("Found max id from table [" + rorTable + "] of [" + maxId + "]");
      rs.close();

      boolean finished = false;
      int splitCount = 0;
      while (!finished) {
        rs = stmt
          .executeQuery("SELECT id FROM raw_occurrence_record LIMIT " + (splitCount * splitSize) + ", " + splitSize);
        if (rs.next()) {
          rs.last();
          int id = rs.getInt("id");
          if (id == maxId) {
            log.info("Reached maxId of [{}] - quitting", maxId);
            finished = true;
          } else {
            splitCount++;
            log.info("Split # [{}] at id [{}]", splitCount, id);
            writer.write(id + "\n");
            writer.flush();
          }
        } else {
          log.info("Got empty resultset - assuming we're done.");
          finished = true;
        }
        rs.close();
      }
      stmt.close();
      con.close();

      writer.close();
      fos.close();
    } catch (SQLException e) {
      log.warn("SQL Exception", e);
    } catch (FileNotFoundException e) {
      log.error("Couldn't find file [{}] - aborting", fileName, e);
      System.exit(1);
    } catch (UnsupportedEncodingException e) {
      log.error("JVM failure - aborting", e);
      System.exit(1);
    } catch (IOException e) {
      log.warn("IOException when writing to file", e);
    }
  }

  public static void main(String[] args) {
    RorSplitCounter instance = new RorSplitCounter();
    if (args.length < 3) {
      System.out.println("Usage: RorSplitCounter <ror_table_on_mogo> <num_rows_between_splits> <output_filename>");
      System.exit(1);
    }
    instance.generateSplits(args[0], Integer.valueOf(args[1]), args[2]);
//    instance.generateSplits(1000000, "/tmp/ror_splits.txt");
//    instance.generateSplits(2725000, "/tmp/ror_120_splits.txt"); // produces 120 regions
  }
}
