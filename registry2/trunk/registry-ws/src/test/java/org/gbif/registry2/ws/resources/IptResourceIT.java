package org.gbif.registry2.ws.resources;

import org.gbif.api.model.common.paging.PagingRequest;
import org.gbif.api.model.registry2.Contact;
import org.gbif.api.model.registry2.Endpoint;
import org.gbif.api.model.registry2.Installation;
import org.gbif.api.model.registry2.Node;
import org.gbif.api.model.registry2.Organization;
import org.gbif.api.service.registry2.InstallationService;
import org.gbif.api.service.registry2.NodeService;
import org.gbif.api.service.registry2.OrganizationService;
import org.gbif.api.vocabulary.registry2.ContactType;
import org.gbif.api.vocabulary.registry2.EndpointType;
import org.gbif.api.vocabulary.registry2.InstallationType;
import org.gbif.registry2.database.DatabaseInitializer;
import org.gbif.registry2.database.LiquibaseInitializer;
import org.gbif.registry2.grizzly.RegistryServer;
import org.gbif.registry2.guice.RegistryTestModules;
import org.gbif.registry2.utils.Installations;
import org.gbif.registry2.utils.Nodes;
import org.gbif.registry2.utils.Organizations;
import org.gbif.registry2.ws.model.IptInstallation;
import org.gbif.utils.HttpUtil;
import org.gbif.utils.PreemptiveAuthenticationInterceptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.google.inject.Injector;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class IptResourceIT {

  private static Logger LOG = LoggerFactory.getLogger(IptResourceIT.class);

  // Flushes the database on each run
  @ClassRule
  public static final LiquibaseInitializer liquibaseRule = new LiquibaseInitializer(RegistryTestModules.database());

  @ClassRule
  public static final RegistryServer registryServer = RegistryServer.INSTANCE;

  @Rule
  public final DatabaseInitializer databaseRule = new DatabaseInitializer(RegistryTestModules.database());

  private static HttpUtil http;
  private static SAXParser saxParser;
  private RegistryEntryHandler newRegistryEntryHandler = new RegistryEntryHandler();
  private final NodeService nodeService;
  private final OrganizationService organizationService;
  private final InstallationService installationService;

  // set of HTTP form parameters sent in POST request
  private static final String IPT_NAME = "Test IPT Registry2";
  private static final String IPT_DESCRIPTION = "Description of Test IPT";
  private static final String IPT_PRIMARY_CONTACT_TYPE = "technical";
  private static final String IPT_PRIMARY_CONTACT_NAME = "Kyle Braak";
  private static final String IPT_PRIMARY_CONTACT_EMAIL = "kbraak@gbif.org";
  private static final String IPT_SERVICE_TYPE = "RSS";
  private static final String IPT_SERVICE_URL = "http://ipt.gbif.org/rss.do";
  private static final String IPT_WS_PASSWORD = "password";

  public IptResourceIT() throws ParserConfigurationException, SAXException {
    Injector i = RegistryTestModules.webservice();
    this.nodeService = i.getInstance(NodeResource.class);
    this.organizationService = i.getInstance(OrganizationResource.class);
    this.installationService = i.getInstance(InstallationService.class);
    saxParser = getNsAwareSaxParserFactory().newSAXParser();
  }

  /**
   * Initializes and configures the HttpUtil instance, which is what the IPT uses to issue its HTTP requests. This
   * method copies the same configuration as the IPT uses, to ensure the exact same structured requests are sent during
   * unit testing.
   */
  @BeforeClass
  public static void getHttp() throws ParserConfigurationException, SAXException {
    // new threadsafe, multithreaded http client with support for http and https.
    DefaultHttpClient client = HttpUtil.newMultithreadedClient(100000, 1, 1);
    // the registry requires Preemptive authentication, so make this the very first interceptor in the protocol chain
    client.addRequestInterceptor(new PreemptiveAuthenticationInterceptor(), 0);
    http = new HttpUtil(client);
  }

  /**
   * The test begins by persisting a new Organization.
   * </br>
   * Then, it sends a register IPT (POST) request to create a new Installation associated to this organization.
   * The request is issued against the web services running on the local Grizzly test server. The request is sent in
   * exactly the same way as the IPT would send it, using the URL path (/ipt/register), URL encoded form parameters,
   * and basic authentication. The web service authorizes the request, and then persists the Installation, associated
   * to the Organization.
   * </br>
   * Upon receiving an HTTP Response, the test parses its XML content in order to extract the registered IPT UUID for
   * example. The content is parsed exactly the same way as the IPT would do it.
   * </br>
   * Last, the test validates that the installation was persisted correctly.
   */
  @Test
  public void testRegisterIpt() throws IOException, URISyntaxException, SAXException {
    // persist new organization (IPT hosting organization)
    Organization organization = newOrganization();
    UUID organizationKey = organization.getKey();

    // populate params for ws
    List<NameValuePair> data = buildIPTParameters(organization.getKey());

    UrlEncodedFormEntity uefe = new UrlEncodedFormEntity(data, Charset.forName("UTF-8"));

    // construct request uri
    String uri = getRequestUri("/ipt/register");

    // send POST request with credentials
    HttpUtil.Response result = http.post(uri, null, null, orgCredentials(organization), uefe);

    // parse newly registered IPT key (UUID)
    saxParser.parse(getStream(result.content), newRegistryEntryHandler);
    assertNotNull("Registered IPT key should be in response", newRegistryEntryHandler.key);
    assertNotNull("Registered IPT organizationKey should be in response", newRegistryEntryHandler.organisationKey);

    // some information that should have been updated
    Installation installation =
      validatePersistedInstallation(UUID.fromString(newRegistryEntryHandler.key), organizationKey);

    // some additional information to check
    assertEquals(IptInstallation.USER, installation.getCreatedBy());
    assertEquals(IptInstallation.USER, installation.getModifiedBy());
  }

  /**
   * The test begins by persisting a new Organization, and Installation associated to the Organization.
   * </br>
   * Then, it sends an update IPT (POST) request to update the same Installation.  The request is issued against the
   * web services running on the local Grizzly test server. The request is sent in exactly the same way as the IPT
   * would send it, using the URL path (/ipt/update/{key}), URL encoded form parameters, and basic authentication. The
   * web service authorizes the request, and then persists the Installation, updating its information.
   * </br>
   * Upon receiving an HTTP Response, the test parses its XML content in order to extract the registered IPT UUID for
   * example. The content is parsed exactly the same way as the IPT would do it.
   * </br>
   * Next, the test validates that the Installation's information was updated correctly. The same request is then
   * resent once more, and the test validates that no duplicate installation, contact, or endpoint was created.
   */
  @Test
  public void testUpdateIpt() throws IOException, URISyntaxException, SAXException {
    // persist new organization (IPT hosting organization)
    Organization organization = newOrganization();
    UUID organizationKey = organization.getKey();

    // persist new installation of type IPT
    Installation installation = newInstallation(organization);
    UUID installationKey = installation.getKey();

    // validate it
    validateExistingInstallation(installation, organizationKey);

    // some information never going to change
    Date created = installation.getCreated();
    assertNotNull(created);
    String createdBy = installation.getCreatedBy();
    assertNotEquals(IptInstallation.USER, createdBy);

    // populate params for ws
    List<NameValuePair> data = buildIPTParameters(organizationKey);

    UrlEncodedFormEntity uefe = new UrlEncodedFormEntity(data, Charset.forName("UTF-8"));

    // construct request uri
    String uri = getRequestUri("/ipt/update/" + installationKey.toString());

    // send POST request with credentials
    HttpUtil.Response result = http.post(uri, null, null, iptCredentials(installation), uefe);

    // parse updated registered IPT key (UUID)
    saxParser.parse(getStream(result.content), newRegistryEntryHandler);
    assertNotNull("Updated IPT key should be in response", newRegistryEntryHandler.key);
    assertEquals(installationKey.toString(), newRegistryEntryHandler.key);
    assertNotNull("Updated IPT organizationKey should be in response", newRegistryEntryHandler.organisationKey);
    assertEquals(organizationKey.toString(), newRegistryEntryHandler.organisationKey);

    // some information that should have been updated
    installation = validatePersistedInstallation(installationKey, organizationKey);

    // some additional information that should not have been updated
    assertEquals(created, installation.getCreated());
    assertEquals(createdBy, installation.getCreatedBy());

    // before sending the same POST request, count the number of installations, contacts and endpoints
    assertEquals(1, installationService.list(new PagingRequest(0, 10)).getResults().size());
    assertEquals(1, installation.getContacts().size());
    assertEquals(1, installation.getEndpoints().size());

    // send same POST request again, to check that duplicate contact and endpoints don't get persisted
    http.post(uri, null, null, iptCredentials(installation), uefe);

    // retrieve newly updated installation, and make sure the same number of installations, contacts and endpoints exist
    assertEquals(1, installationService.list(new PagingRequest(0, 10)).getResults().size());
    // TODO uncomment assertions below when contact and endpoint are not being duplicated on updates
    // installation = validatePersistedInstallation(installationKey, organizationKey);
    //assertEquals(1, installation.getContacts().size());
    //assertEquals(1, installation.getEndpoints().size());
  }

  /**
   * The test sends a register IPT (POST) request to create a new Installation, however, it is missing a mandatory HTTP
   * Parameter for the hosting organization key. The test must check that the server responds with a 400 Unauthorized
   * Response.
   */
  @Test
  public void testRegisterIptButNotAuthorized() throws IOException, URISyntaxException, SAXException {
    // persist new organization (IPT hosting organization)
    Organization organization = newOrganization();

    // populate params for ws
    List<NameValuePair> data = buildIPTParameters(organization.getKey());

    assertEquals(9, data.size());
    // remove mandatory hosting organization key/value before sending
    Iterator<NameValuePair> iter = data.iterator();
    while (iter.hasNext()) {
      NameValuePair pair = iter.next();
      if (pair.getName().equals("organisationKey")) {
        iter.remove();
      }
    }
    assertEquals(8, data.size());

    UrlEncodedFormEntity uefe = new UrlEncodedFormEntity(data, Charset.forName("UTF-8"));

    // construct request uri
    String uri = getRequestUri("/ipt/register");

    // send POST request with WRONG credentials
    // assign the organization the random generated key, to provoke authorization failure
    organization.setKey(UUID.randomUUID());
    HttpUtil.Response result = http.post(uri, null, null, orgCredentials(organization), uefe);

    // 400 expected
    assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), result.getStatusCode());
  }

  /**
   * The test sends a register IPT (POST) request to create a new Installation, however, it is missing a mandatory HTTP
   * Parameter for the primary contact email. The test must check that the server responds with a 401 BAD_REQUEST
   * Response.
   */
  @Test
  public void testRegisterIptWithNoPrimaryContact() throws IOException, URISyntaxException, SAXException {
    // persist new organization (IPT hosting organization)
    Organization organization = newOrganization();

    // populate params for ws
    List<NameValuePair> data = buildIPTParameters(organization.getKey());

    assertEquals(9, data.size());
    // remove mandatory hosting organization key/value before sending
    Iterator<NameValuePair> iter = data.iterator();
    while (iter.hasNext()) {
      NameValuePair pair = iter.next();
      if (pair.getName().equals("primaryContactEmail")) {
        iter.remove();
      }
    }
    assertEquals(8, data.size());

    UrlEncodedFormEntity uefe = new UrlEncodedFormEntity(data, Charset.forName("UTF-8"));

    // construct request uri
    String uri = getRequestUri("/ipt/register");

    // send POST request with credentials so that it passes authorization
    HttpUtil.Response result = http.post(uri, null, null, orgCredentials(organization), uefe);

    // 401 expected
    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatusCode());
  }

  /**
   * Construct request URI, appending path to local Grizzly server address.
   *
   * @param path path
   *
   * @return request URI
   */
  private String getRequestUri(String path) {
    StringBuilder uri = new StringBuilder();
    uri.append("http://localhost:");
    uri.append(RegistryServer.getPort());
    uri.append(path);
    return uri.toString();
  }

  /**
   * Retrieve installation presumed already to exist, and make a series of assertions to ensure it is valid.
   *
   * @param installation    installation
   * @param organizationKey installation hosting organization key
   */
  private void validateExistingInstallation(Installation installation, UUID organizationKey) {
    assertNotNull("Installation should be present", installation);
    assertEquals(organizationKey, installation.getOrganizationKey());
    assertEquals(InstallationType.IPT_INSTALLATION, installation.getType());
    assertNotEquals(IPT_NAME, installation.getTitle());
    assertNotEquals(IPT_DESCRIPTION, installation.getDescription());
    Date modified = installation.getModified();
    assertNotNull(modified);
    String modifiedBy = installation.getModifiedBy();
    assertNotEquals(IptInstallation.USER, modifiedBy);
    assertTrue(installation.getContacts().isEmpty());
    assertTrue(installation.getEndpoints().isEmpty());
  }

  /**
   * Retrieve persisted IPT installation, and make a series of assertions to ensure it has been properly persisted.
   *
   * @param installationKey installation key (UUID)
   * @param organizationKey installation hosting organization key
   *
   * @return validated installation
   */
  private Installation validatePersistedInstallation(UUID installationKey, UUID organizationKey) {
    // retrieve installation anew
    Installation installation = installationService.get(installationKey);

    assertNotNull("Installation should be present", installation);
    assertEquals(organizationKey, installation.getOrganizationKey());
    assertEquals(InstallationType.IPT_INSTALLATION, installation.getType());
    assertEquals(IPT_NAME, installation.getTitle());
    assertEquals(IPT_DESCRIPTION, installation.getDescription());
    assertNotNull(installation.getCreated());
    assertNotNull(installation.getModified());

    // check installation's primary contact was properly persisted
    Contact contact = installation.getContacts().get(0);
    assertNotNull("Installation primary contact should be present", contact);
    assertNotNull(contact.getKey());
    assertTrue(contact.isPrimary());
    assertEquals(IPT_PRIMARY_CONTACT_NAME, contact.getFirstName());
    assertEquals(IPT_PRIMARY_CONTACT_EMAIL, contact.getEmail());
    assertEquals(ContactType.TECHNICAL_POINT_OF_CONTACT, contact.getType());
    assertNotNull(contact.getCreated());
    assertEquals(IptInstallation.USER, contact.getCreatedBy());
    assertNotNull(contact.getModified());
    assertNotNull(IptInstallation.USER, contact.getModifiedBy());

    // check installation's RSS/FEED endpoint was properly persisted
    Endpoint endpoint = installation.getEndpoints().get(0);
    assertNotNull("Installation FEED endpoint should be present", endpoint);
    assertNotNull(endpoint.getKey());
    assertEquals(IPT_SERVICE_URL, endpoint.getUrl());
    assertEquals(EndpointType.FEED, endpoint.getType());
    assertNotNull(endpoint.getCreated());
    assertEquals(IptInstallation.USER, endpoint.getCreatedBy());
    assertNotNull(endpoint.getModified());
    assertNotNull(IptInstallation.USER, endpoint.getModifiedBy());

    return installation;
  }

  /**
   * Configure a non validating, namespace aware, SAXParserFactory and return it.
   * </br>
   * Note: this method is copied directly out of the IPT.
   *
   * @return configured SAXParserFactory
   */
  private static SAXParserFactory getNsAwareSaxParserFactory() {
    SAXParserFactory saxf = null;
    try {
      saxf = SAXParserFactory.newInstance();
      saxf.setValidating(false);
      saxf.setNamespaceAware(true);
    } catch (Exception e) {
      LOG.error("Cant create namespace aware SAX Parser Factory: " + e.getMessage(), e);
    }
    return saxf;
  }

  /**
   * Convert a String into a ByteArrayInputStream.
   * </br>
   * Note: this method is copied directly out of the IPT.
   *
   * @param source String to convert
   *
   * @return ByteArrayInputStream
   */
  private InputStream getStream(String source) {
    return new ByteArrayInputStream(source.getBytes());
  }

  /**
   * Populate a list of name value pairs used in the common ws requests for IPT registrations and updates.
   * </br>
   * Basically a copy of the method in the IPT, to ensure the parameter names are identical.
   *
   * @return list of name value pairs, or an empty list if the IPT or organisation key were null
   */
  private List<NameValuePair> buildIPTParameters(UUID organizationKey) {
    List<NameValuePair> data = new ArrayList<NameValuePair>();
    // main
    data.add(new BasicNameValuePair("organisationKey", organizationKey.toString()));
    data.add(new BasicNameValuePair("name", IPT_NAME));
    data.add(new BasicNameValuePair("description", IPT_DESCRIPTION));

    // primary contact
    data.add(new BasicNameValuePair("primaryContactType", IPT_PRIMARY_CONTACT_TYPE));
    data.add(new BasicNameValuePair("primaryContactName", IPT_PRIMARY_CONTACT_NAME));
    data.add(new BasicNameValuePair("primaryContactEmail", IPT_PRIMARY_CONTACT_EMAIL));

    // service/endpoint
    data.add(new BasicNameValuePair("serviceTypes", IPT_SERVICE_TYPE));
    data.add(new BasicNameValuePair("serviceURLs", IPT_SERVICE_URL));

    // add IPT password used for updating the IPT's own metadata & issuing atomic updateURL operations
    data.add(new BasicNameValuePair("wsPassword", IPT_WS_PASSWORD));

    return data;
  }

  /**
   * Populate credentials used in IPT registration ws request.
   * </br>
   * Note: this method is copied directly out of the IPT.
   *
   * @param org Organization to which IPT will be associated
   *
   * @return credentials
   */
  private UsernamePasswordCredentials orgCredentials(Organization org) {
    return new UsernamePasswordCredentials(org.getKey().toString(), org.getPassword());
  }

  /**
   * Populate credentials used in IPT update ws request.
   * TODO: use ipt.getWsPassword() in place of hard coded "password"
   * </br>
   * Note: this method is copied directly out of the IPT.
   *
   * @param ipt IPT Installation
   *
   * @return credentials
   */
  private UsernamePasswordCredentials iptCredentials(Installation ipt) {
    return new UsernamePasswordCredentials(ipt.getKey().toString(), "password");
  }

  /**
   * Persist a new Organization for use in Unit Tests.
   *
   * @return Organization created
   */
  private Organization newOrganization() {
    UUID key = nodeService.create(Nodes.newInstance());
    Node node = nodeService.get(key);
    Organization o = Organizations.newInstance(node.getKey());
    organizationService.create(o);
    return o;
  }

  /**
   * Persist a new Installation associated to a hosting organization for use in Unit Tests.
   *
   * @param organization hosting organization
   *
   * @return Installation created
   */
  private Installation newInstallation(Organization organization) {
    Installation i = Installations.newInstance(organization.getKey());
    UUID key = installationService.create(i);
    // some properties like created, modified are only set when the installation is retrieved anew
    return installationService.get(key);
  }

  /**
   * Super simple SAX handler that extracts all element and attribute content from any XML document. The resulting
   * string is concatenating all content and inserts a space at every element or attribute start.
   * </br>
   * Note: this class is copied directly out of the IPT. It also uses a commons-lang3 dependency.
   */
  private class RegistryEntryHandler extends DefaultHandler {

    private String content;
    public String organisationKey;
    public String resourceKey;
    public String serviceKey;
    public String password;
    public String key;

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
      content += String.valueOf(ArrayUtils.subarray(ch, start, start + length));
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
      if (name.equalsIgnoreCase("key")) {
        key = content.replaceAll("\\s", "");
      } else if (name.equalsIgnoreCase("organisationKey") || name.equalsIgnoreCase("organizationKey")) {
        organisationKey = content.replaceAll("\\s", "");
      } else if (name.equalsIgnoreCase("resourceKey")) {
        resourceKey = content.replaceAll("\\s", "");
      } else if (name.equalsIgnoreCase("serviceKey")) {
        serviceKey = content.replaceAll("\\s", "");
      }
      content = "";
    }

    @Override
    public void startDocument() throws SAXException {
      content = "";
      key = "";
      organisationKey = "";
      resourceKey = "";
      serviceKey = "";
      password = "";
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
      content = "";
    }
  }
}
