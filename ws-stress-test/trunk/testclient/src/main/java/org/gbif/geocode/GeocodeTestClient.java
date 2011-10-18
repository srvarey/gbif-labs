package org.gbif.geocode;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.io.CharStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.collect.Lists.newArrayList;

public class GeocodeTestClient {

  private static final Logger LOG = LoggerFactory.getLogger(GeocodeTestClient.class);

  private static class CommandLineArgs {

    public static final String NUM_WAVES = "-waves";
    @Parameter(names = {NUM_WAVES})
    public Integer waves = 2;

    public static final String JVMS_PER_WAVE = "-jvmsperwave";
    @Parameter(names = {JVMS_PER_WAVE})
    public Integer jvmsPerWave = 52;

    public static final String WS_CALLS = "-wscalls";
    @Parameter(names = {WS_CALLS})
    public Integer wsCalls = 1;
  }

  private static class JvmExecutor implements Runnable {

    private final Integer retries;

    private JvmExecutor(Integer retries) {
      this.retries = retries;
    }

    @Override
    public void run() {
      String separator = System.getProperty("file.separator");
      String classpath = System.getProperty("java.class.path");
      String path = System.getProperty("java.home") + separator + "bin" + separator + "java";

      ProcessBuilder builder =
        new ProcessBuilder(path, "-cp", classpath, WsClient.class.getCanonicalName(), retries.toString());
      builder.redirectErrorStream(true);
      Process process;
      String output = null;
      try {
        LOG.debug("Starting child JVM");
        process = builder.start();
        process.waitFor();
        LOG.info("Child finished");
        output = CharStreams.toString(new InputStreamReader(process.getInputStream()));
      } catch (IOException e) {
        LOG.error("Caugh exception", e);
      } catch (InterruptedException e) {
        LOG.error("Caugh exception", e);
      } catch (Exception e) {
        LOG.error("Caught unexpected EXception", e);
      }
      LOG.debug("Finshed child JVM: {}", output.trim());
    }
  }

  public static void main(String[] args) throws InterruptedException {
    LOG.info("Starting Test client");

    CommandLineArgs commandLineArgs = new CommandLineArgs();
    new JCommander(commandLineArgs, args);

    ExecutorService executorService = Executors.newFixedThreadPool(commandLineArgs.jvmsPerWave);
    for (int i = 0; i < commandLineArgs.waves; i++) {
      Collection<Callable<Object>> tasks = newArrayList();
      LOG.info("Wave {}:", i + 1);
      LOG.info("--> Initializing tasks");
      for (int j = 0; j < commandLineArgs.jvmsPerWave; j++) {
        LOG.debug("--> Starting JVM #{}", j + 1);
        tasks.add(Executors.callable(new JvmExecutor(commandLineArgs.wsCalls)));
      }
      LOG.info("--> Waiting for wave to finish");
      executorService.invokeAll(tasks);
      LOG.info("--> Wave #{} finished", i + 1);
    }

    executorService.shutdown();
    executorService.awaitTermination(1, TimeUnit.MINUTES);
    LOG.info("Test client finished. Ran {} waves, {} JVM per wave, {} WS calls per JVM",
      new Object[] {commandLineArgs.waves, commandLineArgs.jvmsPerWave, commandLineArgs.wsCalls});
  }

}
