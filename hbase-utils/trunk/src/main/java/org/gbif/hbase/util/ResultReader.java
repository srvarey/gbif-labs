package org.gbif.hbase.util;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Wraps an HBase Result object and provides convenience methods for get typed values out for
 * a given cell. Thread safe.
 */
public class ResultReader {

  private final Result row;
  private byte[] cf;

  /**
   * Construct with only a row if you're going to specify the column family in each request.
   */
  public ResultReader(Result row) {
    this.row = row;
  }

  /**
   * Construct with both row and column family if you wish to use that cf as the default in all method calls. The cf
   * can still be overridden by using any of the methods that take a cf parameter.
   */
  public ResultReader(Result row, String columnFamily) {
    this.row = row;
    this.cf = Bytes.toBytes(columnFamily);
  }

  /**
   * Uses the default column family as set in the constructor.
   *
   * @param columnName   column or "qualifier"
   * @param defaultValue returned if value at columnName is null
   *
   * @return the value from the specified column, or defaultValue if it's null/doesn't exist
   */
  public String getString(String columnName, String defaultValue) {
    return getString(cf, columnName, defaultValue);
  }

  public String getString(String columnFamily, String columnName, String defaultValue) {
    return getString(Bytes.toBytes(columnFamily), columnName, defaultValue);
  }

  public String getString(byte[] columnFamily, String columnName, String defaultValue) {
    KeyValue raw = row.getColumnLatest(columnFamily, Bytes.toBytes(columnName));
    String result = (raw == null) ? defaultValue : Bytes.toString(raw.getValue());

    return result;
  }

  /**
   * Uses the default column family as set in the constructor.
   *
   * @param columnName   column or "qualifier"
   * @param defaultValue returned if value at columnName is null
   *
   * @return the value from the specified column, or defaultValue if it's null/doesn't exist
   */
  public Integer getInteger(String columnName, Integer defaultValue) {
    return getInteger(cf, columnName, defaultValue);
  }

  public Integer getInteger(String columnFamily, String columnName, Integer defaultValue) {
    return getInteger(Bytes.toBytes(columnFamily), columnName, defaultValue);
  }

  public Integer getInteger(byte[] columnFamily, String columnName, Integer defaultValue) {
    KeyValue raw = row.getColumnLatest(columnFamily, Bytes.toBytes(columnName));
    Integer result = (raw == null) ? defaultValue : Integer.valueOf(Bytes.toInt(raw.getValue()));

    return result;
  }

  /**
   * Uses the default column family as set in the constructor.
   *
   * @param columnName   column or "qualifier"
   * @param defaultValue returned if value at columnName is null
   *
   * @return the value from the specified column, or defaultValue if it's null/doesn't exist
   */
  public Long getLong(String columnName, Long defaultValue) {
    return getLong(cf, columnName, defaultValue);
  }

  public Long getLong(String columnFamily, String columnName, Long defaultValue) {
    return getLong(Bytes.toBytes(columnFamily), columnName, defaultValue);
  }

  public Long getLong(byte[] columnFamily, String columnName, Long defaultValue) {
    KeyValue raw = row.getColumnLatest(columnFamily, Bytes.toBytes(columnName));
    Long result = (raw == null) ? defaultValue : Long.valueOf(Bytes.toLong(raw.getValue()));

    return result;
  }

  /**
   * Uses the default column family as set in the constructor.
   *
   * @param columnName   column or "qualifier"
   * @param defaultValue returned if value at columnName is null
   *
   * @return the value from the specified column, or defaultValue if it's null/doesn't exist
   */
  public Double getDouble(String columnName, Double defaultValue) {
    return getDouble(cf, columnName, defaultValue);
  }

  public Double getDouble(String columnFamily, String columnName, Double defaultValue) {
    return getDouble(Bytes.toBytes(columnFamily), columnName, defaultValue);
  }

  public Double getDouble(byte[] columnFamily, String columnName, Double defaultValue) {
    KeyValue raw = row.getColumnLatest(columnFamily, Bytes.toBytes(columnName));
    Double result = (raw == null) ? defaultValue : Double.valueOf(Bytes.toDouble(raw.getValue()));

    return result;
  }
}
