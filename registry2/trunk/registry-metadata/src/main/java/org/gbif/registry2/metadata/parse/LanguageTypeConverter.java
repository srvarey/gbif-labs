package org.gbif.registry2.metadata.parse;

import org.gbif.api.util.VocabularyUtils;
import org.gbif.api.vocabulary.Language;

import java.util.Locale;

import org.apache.commons.beanutils.converters.AbstractConverter;
import org.apache.commons.lang3.LocaleUtils;

/**
 * {@link org.apache.commons.beanutils.Converter} implementation that handles conversion
 * to and from <b>Language</b> ENUM objects.
 */
public class LanguageTypeConverter extends AbstractConverter {

  /**
   * Construct a <b>LanguageTypeConverter</b> <i>Converter</i>.
   */
  public LanguageTypeConverter() {
  }

  public LanguageTypeConverter(Language defaultValue) {
    super(defaultValue);
  }

  /**
   * Convert a String into a Country ENUM. The Country type is determined from the input String. If the
   * conversion was not successful, null is returned.
   *
   * @param type  Data type to which this value should be converted.
   * @param value The input value to be converted.
   *
   * @return The Country ENUM, or null if the conversion was not successful.
   */
  @Override
  protected Object convertToType(Class type, Object value) {
    Language l = null;
    try {
      return VocabularyUtils.lookupEnum(value.toString(), Language.class);

    } catch (IllegalArgumentException e) {
      l = Language.fromIsoCode(value.toString());
      if (Language.UNKNOWN == l) {
        // check if we got a Locale and use only the language part
        Locale loc = LocaleUtils.toLocale(value.toString());
        if (loc != null) {
          l = Language.fromIsoCode(loc.getISO3Language());
        }
      }
      return l;
    }
  }

  /**
   * Return the default type this {@code Converter} handles.
   *
   * @return The default type this {@code Converter} handles.
   */
  @Override
  protected Class getDefaultType() {
    return Language.class;
  }
}
