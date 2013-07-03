package org.gbif.registry2.ws.model;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class used to generate error response for legacy (GBRDS/IPT) API.
 * </br>
 * JAXB annotations allow the class to be converted into an XML document or JSON response. @XmlElement is used to
 * specify element names that consumers of legacy services expect to find.
 */
@XmlRootElement(name = "error")
public class ErrorResponse {

  @XmlAttribute
  private String error;

  public ErrorResponse(String error) {
    this.error = error;
  }

  @NotNull
  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }
}
