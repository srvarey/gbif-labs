/***************************************************************************
 * Copyright 2010 Global Biodiversity Information Facility Secretariat
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ***************************************************************************/

package org.gbif.occurrence.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang.text.StrTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author markus
 *
 */
public class CSVReader implements ClosableIterator<String[]>, Iterable<String[]> {
  private static final Logger LOG = LoggerFactory.getLogger(CSVReader.class);
  public final int headerRows;
  public final String encoding;
  public final String delimiter;
  public final Character quoteChar;
  public final String[] header;
  private final StrTokenizer tokenizer;
  private String row;
  private final BufferedReader br;

  public CSVReader(File source, String encoding, String delimiter, Character quotes, Integer headerRows) throws IOException{
    this.delimiter=delimiter;
    this.encoding=encoding;
    this.quoteChar=quotes;
    if (headerRows==null || headerRows<0){
      this.headerRows=0;
    }else{
      this.headerRows=headerRows;
    }
    tokenizer = new StrTokenizer();
    tokenizer.setDelimiterString(delimiter);
    if (quotes!=null){
      tokenizer.setQuoteChar(quotes);
    }
    tokenizer.setIgnoreEmptyTokens(false);
    tokenizer.reset();
    InputStream fis = null;    
    if (source.getName().endsWith(".gz")) {
    	fis = new GZIPInputStream(new FileInputStream(source));
    } else {
    	fis = new FileInputStream(source);
    }
    InputStreamReader reader = new InputStreamReader(fis, encoding);
    br = new BufferedReader(reader);
    row=br.readLine();
    // parse header row
    if (row==null){
      header=null;
    }else{
      tokenizer.reset(row);
      header = tokenizer.getTokenArray();
    }
    // skip initial header rows?
    while (headerRows>0){
      headerRows--;
      row=br.readLine();
    }
  }

  /* (non-Javadoc)
   * @see org.gbif.file.ClosableIterator#close()
   */
  public void close() {
    try {
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  /* (non-Javadoc)
   * @see java.util.Iterator#hasNext()
   */
  public boolean hasNext() {
    return row!=null;
  }
  /* (non-Javadoc)
   * @see java.lang.Iterable#iterator()
   */
  public ClosableIterator<String[]> iterator() {
    return this;
  }
  /* (non-Javadoc)
   * @see java.util.Iterator#next()
   */
  public String[] next() {
    if (row==null){
      return null;
    }
    tokenizer.reset(row);
    try {
      row=br.readLine();
      // skip empty lines
      while(row!=null && row.equals("")){
        row=br.readLine();
      }
    } catch (IOException e) {
      row=null;
      e.printStackTrace();
    }
    return tokenizer.getTokenArray();
  }
  /* (non-Javadoc)
   * @see java.util.Iterator#remove()
   */
  public void remove() {
    throw new UnsupportedOperationException("Remove not supported");
  }
}
