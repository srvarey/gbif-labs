package org.gbif.hbase.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ResultReaderTest {

  private static final String CF1_NAME = "1";
  private static final byte[] CF1 = Bytes.toBytes(CF1_NAME);
  private static final String CF2_NAME = "2";
  private static final byte[] CF2 = Bytes.toBytes(CF2_NAME);
  private static final String INT_COL_NAME = "a";
  private static final byte[] INT_COL = Bytes.toBytes(INT_COL_NAME);
  private static final String DOUBLE_COL_NAME = "b";
  private static final byte[] DOUBLE_COL = Bytes.toBytes(DOUBLE_COL_NAME);
  private static final String LONG_COL_NAME = "c";
  private static final byte[] LONG_COL = Bytes.toBytes(LONG_COL_NAME);
  private static final String STRING_COL_NAME = "d";
  private static final byte[] STRING_COL = Bytes.toBytes(STRING_COL_NAME);
  private static final byte[] KEY = Bytes.toBytes("12345");

  private static final int INT_VAL_1 = 1111;
  private static final double DOUBLE_VAL_1 = 2.2222222222222222222d;
  private static final long LONG_VAL_1 = 33333333333333l;
  private static final String STRING_VAL_1 = "not numbers";

  private static final int INT_VAL_2 = 4444;
  private static final double DOUBLE_VAL_2 = 5.55555555555555d;
  private static final long LONG_VAL_2 = 66666666666666666l;
  private static final String STRING_VAL_2 = "just a string";

  private Result result = null;

  @Before
  public void setup() {
    List<KeyValue> kvs = new ArrayList<KeyValue>();

    // CF1
    KeyValue kv = new KeyValue(KEY, CF1, INT_COL, Bytes.toBytes(INT_VAL_1));
    kvs.add(kv);
    kv = new KeyValue(KEY, CF1, DOUBLE_COL, Bytes.toBytes(DOUBLE_VAL_1));
    kvs.add(kv);
    kv = new KeyValue(KEY, CF1, LONG_COL, Bytes.toBytes(LONG_VAL_1));
    kvs.add(kv);
    kv = new KeyValue(KEY, CF1, STRING_COL, Bytes.toBytes(STRING_VAL_1));
    kvs.add(kv);

    // CF2
    kv = new KeyValue(KEY, CF2, INT_COL, Bytes.toBytes(INT_VAL_2));
    kvs.add(kv);
    kv = new KeyValue(KEY, CF2, DOUBLE_COL, Bytes.toBytes(DOUBLE_VAL_2));
    kvs.add(kv);
    kv = new KeyValue(KEY, CF2, LONG_COL, Bytes.toBytes(LONG_VAL_2));
    kvs.add(kv);
    kv = new KeyValue(KEY, CF2, STRING_COL, Bytes.toBytes(STRING_VAL_2));
    kvs.add(kv);
    result = new Result(kvs);
  }

  @Test
  public void testString() {

    String test = ResultReader.getString(result, CF1_NAME, STRING_COL_NAME, null);
    assertTrue(STRING_VAL_1.equals(test));

    test = ResultReader.getString(result, CF1_NAME, "fake col", "a default value");
    assertTrue("a default value".equals(test));

    test = ResultReader.getString(result, CF2_NAME, STRING_COL_NAME, null);
    assertTrue(STRING_VAL_2.equals(test));
  }

  @Test
  public void testDouble() {
    Double test = ResultReader.getDouble(result, CF1_NAME, DOUBLE_COL_NAME, null);
    assertEquals(DOUBLE_VAL_1, test);

    test = ResultReader.getDouble(result, CF1_NAME, "fake col", 123456.789d);
    assertEquals(Double.valueOf(123456.789), test);

    test = ResultReader.getDouble(result, CF2_NAME, DOUBLE_COL_NAME, null);
    assertEquals(DOUBLE_VAL_2, test);
  }

  @Test
  public void testInteger() {
    Integer test = ResultReader.getInteger(result, CF1_NAME, INT_COL_NAME, null);
    assertEquals(INT_VAL_1, test.intValue());

    test = ResultReader.getInteger(result, CF1_NAME, "fake col", 123456);
    assertEquals(Integer.valueOf(123456), test);

    test = ResultReader.getInteger(result, CF2_NAME, INT_COL_NAME, null);
    assertEquals(INT_VAL_2, test.intValue());
  }

  @Test
  public void testLong() {
    Long test = ResultReader.getLong(result, CF1_NAME, LONG_COL_NAME, null);
    assertEquals(LONG_VAL_1, test.longValue());

    test = ResultReader.getLong(result, CF1_NAME, "fake col", 1234567890l);
    assertEquals(Long.valueOf(1234567890l), test);

    test = ResultReader.getLong(result, CF2_NAME, LONG_COL_NAME, null);
    assertEquals(LONG_VAL_2, test.longValue());
  }
}
