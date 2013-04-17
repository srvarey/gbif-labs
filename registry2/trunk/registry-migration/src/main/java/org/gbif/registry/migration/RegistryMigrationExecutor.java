package org.gbif.registry.migration;

import java.io.File;

import scriptella.execution.EtlExecutor;
import scriptella.execution.EtlExecutorException;
import scriptella.execution.ExecutionStatistics;
import scriptella.interactive.ConsoleProgressIndicator;

public class RegistryMigrationExecutor {

  private static ExecutionStatistics execute(String scriptFile, String consoleIndicator) throws EtlExecutorException {
    ExecutionStatistics statics;
    statics = EtlExecutor.newExecutor(new File(scriptFile)).execute(new ConsoleProgressIndicator(consoleIndicator));
    return statics;
  }

  public static void main(String[] args) {
    try {

      ExecutionStatistics statics = null;

      System.out.println("Starting migration...");
      statics = execute("src/main/resources/startup.xml", "Registry node");

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
      
      System.out.println("Starting node_contacts creation...");
      statics = execute("src/main/resources/migrate-node_contacts.xml", "Registry node_contact");
      System.out.println("Node_contacts created in " + statics.getTotalTime() + " milliseconds");
      
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

      
    } catch (EtlExecutorException e) {
      e.printStackTrace();
    }
  }
}
