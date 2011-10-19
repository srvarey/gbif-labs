/**
 *
 */
package mapreduce;

/**
 * Represents a location
 * Ported from the geocode-ws
 * TODO: change this to a dependency
 *
 * @author tim
 */
public class Location {

  protected String id;
  protected String type;
  protected String source;
  protected String title;
  protected String isoCountryCode2Digit;

  public Location() {
  }

  public Location(String id, String type, String source, String title, String isoCountryCode2Digit) {
    super();
    this.id = id;
    this.type = type;
    this.source = source;
    this.title = title;
    this.isoCountryCode2Digit = isoCountryCode2Digit;
  }

  public String getIsoCountryCode2Digit() {
    return isoCountryCode2Digit;
  }

  public void setIsoCountryCode2Digit(String isoCountryCode2Digit) {
    this.isoCountryCode2Digit = isoCountryCode2Digit;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }
}
