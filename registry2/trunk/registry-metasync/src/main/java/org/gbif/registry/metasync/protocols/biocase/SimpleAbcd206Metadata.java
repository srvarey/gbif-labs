package org.gbif.registry.metasync.protocols.biocase;

import java.net.URI;

import org.apache.commons.digester3.annotations.rules.BeanPropertySetter;
import org.apache.commons.digester3.annotations.rules.ObjectCreate;

@ObjectCreate(pattern = "response/content/DataSets/DataSet")
public class SimpleAbcd206Metadata {

  private static final String BASE_PATH = "response/content/DataSets/DataSet/";

  @BeanPropertySetter(pattern = BASE_PATH + "Metadata/Description/Representation/Title")
  private String name;

  @BeanPropertySetter(pattern = BASE_PATH + "Metadata/Description/Representation/Details")
  private String details;

  @BeanPropertySetter(pattern = BASE_PATH + "Metadata/Description/Representation/URI")
  private URI homepage;

  @BeanPropertySetter(pattern = BASE_PATH + "Metadata/Owners/Owner/LogoURI")
  private URI logoUrl;

  @BeanPropertySetter(pattern = BASE_PATH + "Metadata/Owners/Owner/Addresses/Address")
  private String address;

  @BeanPropertySetter(pattern = BASE_PATH + "Metadata/Owners/Owner/EmailAddresses/EmailAddress")
  private String email;

  @BeanPropertySetter(pattern = BASE_PATH + "Metadata/Owners/Owner/TelephoneNumbers/TelephoneNumber/Number")
  private String phone;

  @BeanPropertySetter(pattern = BASE_PATH + "Metadata/IPRStatements/Copyrights/Copyright/Text")
  private String rights;

  @BeanPropertySetter(pattern = BASE_PATH + "Metadata/IPRStatements/Citations/Citation/Text")
  private String citationText;

  @BeanPropertySetter(pattern = BASE_PATH + "Units/Unit[0]/RecordBasis")
  private String basisOfRecord;

}


/*


		<!-- Metadata properties -->
		<pattern value="Metadata/IPRStatements/TermsOfUseStatements/TermsOfUse">
			<call-method-rule methodname="addMetadataProperty" paramcount="2"/>
			<object-param-rule paramnumber="0" value="TermsOfUse" type="java.lang.String"/>
			<call-param-rule paramnumber="1" type="java.lang.String"/>
		</pattern>
		<pattern value="Metadata/IPRDeclarations/IPRDeclaration">
			<call-method-rule methodname="addMetadataProperty" paramcount="2"/>
			<object-param-rule paramnumber="0" value="IPRDeclaration" type="java.lang.String"/>
			<call-param-rule paramnumber="1" type="java.lang.String"/>
		</pattern>
		<pattern value="Metadata/Disclaimers/Disclaimer">
			<call-method-rule methodname="addMetadataProperty" paramcount="2"/>
			<object-param-rule paramnumber="0" value="Disclaimer" type="java.lang.String"/>
			<call-param-rule paramnumber="1" type="java.lang.String"/>
		</pattern>
		<pattern value="Metadata/Owners/Owner/Organisation/Name/Representation/Text">
			<call-method-rule methodname="addMetadataProperty" paramcount="2"/>
			<object-param-rule paramnumber="0" value="OrganisationName" type="java.lang.String"/>
			<call-param-rule paramnumber="1" type="java.lang.String"/>
		</pattern>
		<pattern value="Metadata/Owners/Owner/URIs/URL">
			<call-method-rule methodname="addMetadataProperty" paramcount="2"/>
			<object-param-rule paramnumber="0" value="OwnerURL" type="java.lang.String"/>
			<call-param-rule paramnumber="1" type="java.lang.String"/>
		</pattern>

		<pattern value="TechnicalContacts/TechnicalContact">
	   		<object-create-rule classname="org.gbif.registry.model.Contact"/>
	   		<bean-property-setter-rule propertyname="firstName" pattern="Name"/>
	   		<bean-property-setter-rule propertyname="email" pattern="Email"/>
	   		<bean-property-setter-rule propertyname="phone" pattern="Phone"/>
	   		<bean-property-setter-rule propertyname="address" pattern="Address"/>
	   		<set-next-rule methodname="addTechnicalContact" />
	   	</pattern>
	   	<pattern value="ContentContacts/ContentContact">
	   		<object-create-rule classname="org.gbif.registry.model.Contact"/>
	   		<bean-property-setter-rule propertyname="firstName" pattern="Name"/>
	   		<bean-property-setter-rule propertyname="email" pattern="Email"/>
	   		<bean-property-setter-rule propertyname="phone" pattern="Phone"/>
	   		<bean-property-setter-rule propertyname="address" pattern="Address"/>
	   		<set-next-rule methodname="addAdministrativeContact" />
	   	</pattern>
	</pattern>
</digester-rules>
 */
