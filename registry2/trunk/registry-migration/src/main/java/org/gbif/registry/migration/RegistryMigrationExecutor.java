package org.gbif.registry.migration;

import java.io.File;

import org.slf4j.bridge.SLF4JBridgeHandler;
import scriptella.execution.EtlExecutor;
import scriptella.execution.EtlExecutorException;
import scriptella.execution.ExecutionStatistics;
import scriptella.interactive.ConsoleProgressIndicator;

public class RegistryMigrationExecutor {

  private static ExecutionStatistics execute(String scriptFile, String consoleIndicator) throws EtlExecutorException {
    return EtlExecutor.newExecutor(new File(scriptFile)).execute(new ConsoleProgressIndicator(consoleIndicator));
  }

  public static void main(String[] args) {
    try {
      SLF4JBridgeHandler.removeHandlersForRootLogger();
      SLF4JBridgeHandler.install();

      System.out.println("Starting migration...");
      ExecutionStatistics statics = execute("src/main/resources/startup.xml", "Registry node");

      System.out.println("Starting nodes creation...");
      statics = execute("src/main/resources/migrate-nodes.xml", "Registry node");
      System.out.println("Nodes created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting organizations creation...");
      statics = execute("src/main/resources/migrate-organizations.xml", "Registry organization");
      System.out.println("Organizations created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting installations creation...");
      statics = execute("src/main/resources/migrate-installations.xml", "Registry installation");
      System.out.println("Installations created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting datasets creation...");
      statics = execute("src/main/resources/migrate-datasets.xml", "Registry dataset");
      System.out.println("Datasets created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting networks creation...");
      statics = execute("src/main/resources/migrate-networks.xml", "Registry network");
      System.out.println("Networks created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting dataset_networks creation...");
      statics = execute("src/main/resources/migrate-dataset_networks.xml", "Registry dataset_network");
      System.out.println("Dataset_networks created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting contacts creation...");
      statics = execute("src/main/resources/migrate-contacts.xml", "Registry contact");
      System.out.println("Contacts created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting organization_contacts creation...");
      statics = execute("src/main/resources/migrate-organization_contacts.xml", "Registry organization_contact");
      System.out.println("Organization_contacts created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting dataset_contacts creation...");
      statics = execute("src/main/resources/migrate-dataset_contacts.xml", "Registry dataset_contact");
      System.out.println("Dataset_contacts created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting installation_contacts creation...");
      statics = execute("src/main/resources/migrate-installation_contacts.xml", "Registry installation_contact");
      System.out.println("Installation_contacts created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting network_contacts creation...");
      statics = execute("src/main/resources/migrate-network_contacts.xml", "Registry network_contact");
      System.out.println("Network_contacts created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting endpoints creation...");
      statics = execute("src/main/resources/migrate-endpoints.xml", "Registry endpoint");
      System.out.println("Endpoints created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting organization_endpoints creation...");
      statics = execute("src/main/resources/migrate-organization_endpoints.xml", "Registry organization_endpoint");
      System.out.println("Organization_endpoints created in " + statics.getTotalTime() + " milliseconds");
      
      System.out.println("Starting installation_endpoints creation...");
      statics = execute("src/main/resources/migrate-installation_endpoints.xml", "Registry installation_endpoint");
      System.out.println("Installation_endpoints created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting dataset_endpoints creation...");
      statics = execute("src/main/resources/migrate-dataset_endpoints.xml", "Registry dataset_endpoint");
      System.out.println("Dataset_endpoints created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting network_endpoints creation...");
      statics = execute("src/main/resources/migrate-network_endpoints.xml", "Registry network_endpoint");
      System.out.println("Network_endpoints created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting node_endpoints creation...");
      statics = execute("src/main/resources/migrate-node_endpoints.xml", "Registry node_endpoint");
      System.out.println("Node_endpoints created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting machine_tags creation...");
      statics = execute("src/main/resources/migrate-machine_tags.xml", "Registry machine_tag");
      System.out.println("Machine_tags created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting node_machine_tags creation...");
      statics = execute("src/main/resources/migrate-node_machine_tags.xml", "Registry node_machine_tag");
      System.out.println("Node_machine_tags created in " + statics.getTotalTime() + " milliseconds");
      
      System.out.println("Starting organization_machine_tags creation...");
      statics = execute("src/main/resources/migrate-organization_machine_tags.xml", "Registry organization_machine_tag");
      System.out.println("Organization_machine_tags created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting installation_machine_tags creation...");
      statics = execute("src/main/resources/migrate-installation_machine_tags.xml", "Registry installation_machine_tag");
      System.out.println("Installation_machine_tags created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting dataset_machine_tags creation...");
      statics = execute("src/main/resources/migrate-dataset_machine_tags.xml", "Registry dataset_machine_tag");
      System.out.println("Dataset_machine_tags created in " + statics.getTotalTime() + " milliseconds");
      
      System.out.println("Starting network_machine_tags creation...");
      statics = execute("src/main/resources/migrate-network_machine_tags.xml", "Registry network_machine_tag");
      System.out.println("Network_machine_tags created in " + statics.getTotalTime() + " milliseconds");
      
      System.out.println("Starting tags creation...");
      statics = execute("src/main/resources/migrate-tags.xml", "Registry tag");
      System.out.println("Tags created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting node_tags creation...");
      statics = execute("src/main/resources/migrate-node_tags.xml", "Registry node_tag");
      System.out.println("Node_tags created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting organization_tags creation...");
      statics = execute("src/main/resources/migrate-organization_tags.xml", "Registry organization_tag");
      System.out.println("Organization_tags created in " + statics.getTotalTime() + " milliseconds");
      
      System.out.println("Starting installation_tags creation...");
      statics = execute("src/main/resources/migrate-installation_tags.xml", "Registry installation_tag");
      System.out.println("Installation_tags created in " + statics.getTotalTime() + " milliseconds");
            
      System.out.println("Starting dataset_tags creation...");
      statics = execute("src/main/resources/migrate-dataset_tags.xml", "Registry dataset_tag");
      System.out.println("Dataset_tags created in " + statics.getTotalTime() + " milliseconds");
      
      System.out.println("Starting network_tags creation...");
      statics = execute("src/main/resources/migrate-network_tags.xml", "Registry network_tag");
      System.out.println("Network_tags created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting identifiers creation...");
      statics = execute("src/main/resources/migrate-identifiers.xml", "Registry identifier");
      System.out.println("Identifiers created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting organization_identifiers creation...");
      statics = execute("src/main/resources/migrate-organization_identifiers.xml", "Registry organization_identifier");
      System.out.println("Organization_identifiers created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting dataset_identifiers creation...");
      statics = execute("src/main/resources/migrate-dataset_identifiers.xml", "Registry dataset_identifier");
      System.out.println("Dataset_identifiers created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting pg sequence udpates...");
      statics = execute("src/main/resources/reset-sequences.xml", "Reset pg sequences");
      System.out.println("Postgres sequences updated in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting node IMS identifiers creation...");
      statics = execute("src/main/resources/add-ims-identifiers.xml", "IMS identifier");
      System.out.println("IMS Identifiers created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting updating node continents ...");
      statics = execute("src/main/resources/update-node-continents.xml", "continents");
      System.out.println("Node continents udpated in " + statics.getTotalTime() + " milliseconds");

    } catch (EtlExecutorException e) {
      e.printStackTrace();
    }
  }

}
