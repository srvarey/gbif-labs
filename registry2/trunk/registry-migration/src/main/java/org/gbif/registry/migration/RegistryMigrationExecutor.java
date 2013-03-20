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
      // statics = execute("src/main/resources/startup.xml", "Registry node");

/*      System.out.println("Starting nodes creation...");
      statics = execute("src/main/resources/migrate-nodes.xml", "Registry node");
      System.out.println("Nodes created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting organizations creation...");
      statics = execute("src/main/resources/migrate-organizations.xml", "Registry organization");
      System.out.println("Organizations created in " + statics.getTotalTime() + " milliseconds");

      System.out.println("Starting installations creation...");
      statics = execute("src/main/resources/migrate-installations.xml", "Registry installation");
      System.out.println("Installations created in " + statics.getTotalTime() + " milliseconds");*/

      System.out.println("Starting datasets creation...");
      statics = execute("src/main/resources/migrate-datasets.xml", "Registry dataset");
      System.out.println("Datasets created in " + statics.getTotalTime() + " milliseconds");

    } catch (EtlExecutorException e) {
      e.printStackTrace();
    }
  }
}
