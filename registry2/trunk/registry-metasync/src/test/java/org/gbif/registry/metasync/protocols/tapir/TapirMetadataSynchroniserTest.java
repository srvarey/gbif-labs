package org.gbif.registry.metasync.protocols.tapir;

import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.model.registry2.Endpoint;
import org.gbif.api.model.registry2.Installation;
import org.gbif.api.model.registry2.MachineTag;
import org.gbif.api.vocabulary.registry2.InstallationType;
import org.gbif.registry.metasync.SyncResult;
import org.gbif.registry.metasync.protocols.HttpGetMatcher;
import org.gbif.registry.metasync.util.Constants;

import java.io.IOException;
import java.util.ArrayList;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TapirMetadataSynchroniserTest {

  @Mock
  private HttpClient client;
  private TapirMetadataSynchroniser synchroniser;
  private Installation installation;

  @Before
  public void setup() {
    synchroniser = new TapirMetadataSynchroniser(client);

    installation = new Installation();
    installation.setType(InstallationType.TAPIR_INSTALLATION);
    Endpoint endpoint = new Endpoint();
    endpoint.setUrl("http://localhost/nmr");
    installation.addEndpoint(endpoint);
  }

  @Test
  public void testCanHandle() {
    installation.setType(InstallationType.BIOCASE_INSTALLATION);
    assertThat(synchroniser.canHandle(installation)).isFalse();

    installation.setType(InstallationType.TAPIR_INSTALLATION);
    assertThat(synchroniser.canHandle(installation)).isTrue();
  }

  /**
   * A simple test to see if multiple datasets are parsed successfully.
   */
  @Test
  public void testAddedDatasets() throws Exception {
    when(client.execute(argThat(HttpGetMatcher.matchUrl("http://localhost/nmr?op=capabilities")))).thenReturn(
      prepareResponse(200, "tapir/capabilities1.xml"));
    when(client.execute(argThat(HttpGetMatcher.matchUrl("http://localhost/nmr")))).thenReturn(prepareResponse(200,
                                                                                                              "tapir/metadata1.xml"));
    SyncResult syncResult = synchroniser.syncInstallation(installation, new ArrayList<Dataset>());
    assertThat(syncResult.deletedDatasets).isEmpty();
    assertThat(syncResult.existingDatasets).isEmpty();
    assertThat(syncResult.addedDatasets).hasSize(1);
    assertThat(syncResult.addedDatasets.get(0).getContacts()).hasSize(2);
  }

  @Test
  public void testDeletedDataset() throws Exception {
    Dataset dataset = new Dataset();
    dataset.addMachineTag(MachineTag.newInstance(Constants.METADATA_NAMESPACE, Constants.TAPIR_LOCAL_ID, "foobar"));
    dataset.setTitle("Foobar");

    when(client.execute(argThat(HttpGetMatcher.matchUrl("http://localhost/nmr?op=capabilities")))).thenReturn(
      prepareResponse(200, "tapir/capabilities1.xml"));
    when(client.execute(argThat(HttpGetMatcher.matchUrl("http://localhost/nmr")))).thenReturn(prepareResponse(200,
                                                                                                              "tapir/metadata1.xml"));

    SyncResult syncResult = synchroniser.syncInstallation(installation, Lists.newArrayList(dataset));
    assertThat(syncResult.deletedDatasets).hasSize(1);
    assertThat(syncResult.existingDatasets).isEmpty();
    assertThat(syncResult.addedDatasets).hasSize(1);

    assertThat(syncResult.deletedDatasets.get(0).getTitle()).isEqualTo("Foobar");
  }

  @Test
  public void testUpdatedDataset() throws Exception {
    Dataset dataset = new Dataset();
    dataset.addMachineTag(MachineTag.newInstance(Constants.METADATA_NAMESPACE, Constants.TAPIR_LOCAL_ID, "nmr"));
    dataset.setTitle("Foobar");

    when(client.execute(argThat(HttpGetMatcher.matchUrl("http://localhost/nmr?op=capabilities")))).thenReturn(
      prepareResponse(200, "tapir/capabilities1.xml"));
    when(client.execute(argThat(HttpGetMatcher.matchUrl("http://localhost/nmr")))).thenReturn(prepareResponse(200,
                                                                                                              "tapir/metadata1.xml"));

    SyncResult syncResult = synchroniser.syncInstallation(installation, Lists.newArrayList(dataset));
    assertThat(syncResult.deletedDatasets).describedAs("Deleted datasets").isEmpty();
    assertThat(syncResult.existingDatasets).hasSize(1);
    assertThat(syncResult.addedDatasets).isEmpty();

    assertThat(syncResult.existingDatasets
                 .get(dataset)
                 .getTitle()).isEqualTo("ENGLISHNatural History Museum Rotterdam");
  }

  public HttpResponse prepareResponse(int responseStatus, String fileName) throws IOException {
    HttpResponse response =
      new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), responseStatus, ""));
    response.setStatusCode(responseStatus);
    byte[] bytes = Resources.toByteArray(Resources.getResource(fileName));
    response.setEntity(new ByteArrayEntity(bytes));
    return response;
  }
}
