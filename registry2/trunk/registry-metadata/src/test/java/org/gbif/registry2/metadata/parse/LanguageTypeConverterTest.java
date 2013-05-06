package org.gbif.registry2.metadata.parse;

import org.gbif.api.vocabulary.Language;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LanguageTypeConverterTest {
  private LanguageTypeConverter converter = new LanguageTypeConverter();

  @Test
  public void parseLanguages() {
    assertLang(Language.AFRIKAANS, "af");
    assertLang(Language.AFRIKAANS, "AF");
    assertLang(Language.ENGLISH, "EN");
    assertLang(Language.ENGLISH, "en_US");
    assertLang(Language.ENGLISH, "en_UK");
  }

  private void assertLang(Language l, String value) {
    assertEquals(l, converter.convertToType(Language.class, value));
  }
}
