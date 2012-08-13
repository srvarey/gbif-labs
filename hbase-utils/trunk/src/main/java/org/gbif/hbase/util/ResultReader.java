package org.gbif.hbase.util;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Static utility methods for reading (Java) typed fields from an HBase Result.
 */
public class ResultReader {

  /**
   * Should never be constructed.
   */
  private ResultReader() {
  }

  /**
   * Read the value of this cell and interpret as String.
   *
   * @param row          the HBase Result from which to read
   * @param columnFamily column family that holds the column
   * @param columnName   column or "qualifier"
   * @param defaultValue returned if value at columnName is null
   *
   * @return the value from the specified column, or defaultValue if it's null/doesn't exist
   */
  public static String getString(Result row, String columnFamily, String columnName, String defaultValue) {
    KeyValue raw = row.getColumnLatest(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName));
    String result = (raw == null) ? defaultValue : Bytes.toString(raw.getValue());

    return result;
  }

  /**
   * Read the value of this cell and interpret as Integer.
   *
   * @param row          the HBase Result from which to read
   * @param columnFamily column family that holds the column
   * @param columnName   column or "qualifier"
   * @param defaultValue returned if value at columnName is null
   *
   * @return the value from the specified column, or defaultValue if it's null/doesn't exist
   */
  public static Integer getInteger(Result row, String columnFamily, String columnName, Integer defaultValue) {
    KeyValue raw = row.getColumnLatest(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName));
    Integer result = (raw == null) ? defaultValue : Integer.valueOf(Bytes.toInt(raw.getValue()));

    return result;
  }

  /**
   * Read the value of this cell and interpret as Long.
   *
   * @param row          the HBase Result from which to read
   * @param columnFamily column family that holds the column
   * @param columnName   column or "qualifier"
   * @param defaultValue returned if value at columnName is null
   *
   * @return the value from the specified column, or defaultValue if it's null/doesn't exist
   */
  public static Long getLong(Result row, String columnFamily, String columnName, Long defaultValue) {
    KeyValue raw = row.getColumnLatest(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName));
    Long result = (raw == null) ? defaultValue : Long.valueOf(Bytes.toLong(raw.getValue()));

    return result;
  }

  /**
   * Read the value of this cell and interpret as Double.
   *
   * @param row          the HBase Result from which to read
   * @param columnFamily column family that holds the column
   * @param columnName   column or "qualifier"
   * @param defaultValue returned if value at columnName is null
   *
   * @return the value from the specified column, or defaultValue if it's null/doesn't exist
   */
  public static Double getDouble(Result row, String columnFamily, String columnName, Double defaultValue) {
    KeyValue raw = row.getColumnLatest(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName));
    Double result = (raw == null) ? defaultValue : Double.valueOf(Bytes.toDouble(raw.getValue()));

    return result;
  }

  /**
   * Read the value of this cell and return it uninterpreted as byte[].
   *
   * @param row          the HBase Result from which to read
   * @param columnFamily column family that holds the column
   * @param columnName   column or "qualifier"
   * @param defaultValue returned if value at columnName is null
   *
   * @return the value from the specified column, or defaultValue if it's null/doesn't exist
   */
  public static byte[] getBytes(Result row, String columnFamily, String columnName, byte[] defaultValue) {
    KeyValue raw = row.getColumnLatest(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName));
    byte[] result = (raw == null) ? defaultValue : raw.getValue();

    return result;
  }
}
