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

  private void generateSplits(String targDb, int splitSize, String fileName) {
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    FileOutputStream fos = null;
    OutputStreamWriter writer = null;
    try {
      File outFile = new File(fileName);
      outFile.delete();
      outFile.createNewFile();
      fos = new FileOutputStream(outFile);
      writer = new OutputStreamWriter(fos, "UTF-8");
      String driver = "com.mysql.jdbc.Driver";
      Class.forName(driver);
      con = DriverManager.getConnection("jdbc:mysql://mogo.gbif.org:3306/" + targDb, "hbase", "hbasepassword");
      stmt = con.createStatement();
      stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      stmt.setFetchSize(Integer.MIN_VALUE);

      rs = stmt.executeQuery("SELECT id FROM raw_occurrence_record");
      int splitCount = 0;
      int rowCount = 0;
      while (rs.next()) {
        rowCount++;
        int id = rs.getInt("id");
        if (rowCount % splitSize == 0) {
          splitCount++;
          log.info("Split # [{}] at id [{}]", splitCount, id);
          writer.write(id + "\n");
          writer.flush();
        }
      }
      rs.close();
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
    } catch (ClassNotFoundException e) {
      log.error("Couldn't find mysql driver - aborting", e);
      System.exit(1);
    } finally {
      try {
        if (rs != null) rs.close();
        if (stmt != null) stmt.close();
        if (con != null) con.close();
        if (writer != null) writer.close();
        if (fos != null) fos.close();
      } catch (SQLException e) {
        log.warn("Couldn't close db connections", e);
      } catch (IOException e) {
        log.warn("Couldn't close file connections", e);
      }
    }
  }

  public static void main(String[] args) {
    RorSplitCounter instance = new RorSplitCounter();
    if (args.length < 3) {
      System.out.println("Usage: RorSplitCounter <ror_db_on_mogo> <num_rows_between_splits> <output_filename>");
      System.exit(1);
    }
    instance.generateSplits(args[0], Integer.valueOf(args[1]), args[2]);
    //    instance.generateSplits("portal_rollover", 1000000, "/tmp/ror_splits.txt");
  }
}
