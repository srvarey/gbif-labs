package org.gbif.registry2.metadata.parse;

import org.gbif.api.model.common.InterpretedEnum;
import org.gbif.api.vocabulary.Country;
import org.gbif.common.parsers.ParseResult;
import org.gbif.common.parsers.countryname.InterpretedCountryParser;

import org.apache.commons.beanutils.converters.AbstractConverter;

/**
 * {@link org.apache.commons.beanutils.Converter} implementation that handles conversion
 * to and from <b>Country</b> ENUM objects.
 */
public class CountryTypeConverter extends AbstractConverter {

  /**
   * Construct a <b>CountryTypeConverter</b> <i>Converter</i>.
   */
  public CountryTypeConverter() {
  }

  /**
   * Construct a <b>CountryTypeConverter</b> <i>Converter</i> that returns
   * a Country ENUM.
   *
   * @param defaultValue The default value to be returned
   *                     if the value to be converted is missing or an error
   *                     occurs converting the value.
   */
  public CountryTypeConverter(Object defaultValue) {
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
    ParseResult<InterpretedEnum<String, Country>> result =
      InterpretedCountryParser.getInstance().parse(value.toString());
    return result.getStatus() == ParseResult.STATUS.SUCCESS ? result.getPayload().getInterpreted() : null;
  }

  /**
   * Return the default type this {@code Converter} handles.
   *
   * @return The default type this {@code Converter} handles.
   */
  @Override
  protected Class getDefaultType() {
    return InterpretedEnum.class;
  }
}
