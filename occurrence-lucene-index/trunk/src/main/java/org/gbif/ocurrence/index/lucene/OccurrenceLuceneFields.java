package org.gbif.ocurrence.index.lucene;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;

public class OccurrenceLuceneFields {

  public final static OccurrenceFields[] accFieldsValues = OccurrenceFields.values();
  public final static Field[] fields = new Field[accFieldsValues.length];
  static {
    for (int i = 0; i < accFieldsValues.length; i++) {
      fields[i] = new Field(accFieldsValues[i].name(), "", Store.YES, Index.ANALYZED);
    }
  }
}
