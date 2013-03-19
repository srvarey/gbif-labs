package org.gbif.registry.migration.etl;

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

      System.out.println("Starting nodes creation...");
      statics =
        execute("src/main/resources/registry-to-registry.xml",
          "Registry node");
      System.out.println("Technical installations created in " + statics.getTotalTime() + " milliseconds");

    } catch (EtlExecutorException e) {
      e.printStackTrace();
    }
  }
}
