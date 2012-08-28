package org.gbif.ocurrence.index.lucene;

import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class was implemented to be executed only one time, * and probably be executed by a job scheduler (CRON for
 * example).
 * The behavior of the pool of threads can be customized using the properties file, using this
 * file * is possible define the values for: poolSize, maxPoolSize and keepAliveTime; and additionally for the
 * e-mail
 * configuration (an email will be sent at the end of the
 * process): smtp.server,email.to,email.from,email.subject,email.body,email.cc
 * The thread pool is implemented using an {@link ExecutorCompletionService} and a {@link ThreadPoolExecutor} because
 * some
 * results can be return for each job: errors during the process or any other information.
 * 
 * @author fede
 */
public abstract class ThreadPoolRunner<T> {

  private static class DefaultRejectedExecutionHandler implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable job, ThreadPoolExecutor executor) {
      ThreadPoolRunner.log.error("Job/Thread has been rejected, it will put on queue");
      try {
        executor.getQueue().put(job);
      } catch (InterruptedException e1) {
        log.error("Work discarded, thread was interrupted while waiting for space to schedule: {}", job);
      }
    }
  }

  /**
   * Timer to measure the total time of execution
   */
  private StopWatch stopWatch = new StopWatch();

  /**
   * the number of threads to keep in the pool, even if they are idle.
   */

  protected int poolSize = 0;

  /**
   * the maximum number of threads to allow in the pool.
   */
  protected int maxPoolSize = 0;

  /**
   * when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait
   * for new tasks before terminating.
   */
  protected long keepAliveTime = 0;

  /**
   * Executes each submitted task (agent synchronization) using one of possibly several pooled threads
   */
  protected ThreadPoolExecutor threadPool = null;

  /**
   * Decouples the production of new asynchronous tasks from the consumption of the results of completed tasks
   */
  protected CompletionService<T> completionService;

  /**
   * Queue of elements that will be executed by the {@link ThreadPoolExecutor}
   */
  protected final BlockingQueue<Runnable> queue = new SynchronousQueue<Runnable>();

  /**
   * Configuration properties map
   */
  protected Properties config;

  /**
   * Configuration file name
   */
  private final String CONFIG_FILE;

  /**
   * Logger for the {@link MonitoringJob} class
   */
  protected static Logger log = LoggerFactory.getLogger(ThreadPoolRunner.class);

  /**
   * Default constructor
   */
  public ThreadPoolRunner(String configFile) {
    super();
    this.CONFIG_FILE = configFile;
    this.loadConfig();
  }

  public abstract List<? extends Callable<T>> createJobList();

  public Properties getConfig() {
    return config;
  }

  /**
   * Initialize the {@link ThreadPoolExecutor} and {@link ExecutorCompletionService}
   */
  private void initThreadPool() {
    if (keepAliveTime == -1) {
      log.debug("KeepAliveTime set to unlimited value");
      this.threadPool = new ThreadPoolExecutor(poolSize, maxPoolSize, Long.MAX_VALUE, TimeUnit.NANOSECONDS, queue,
          new DefaultRejectedExecutionHandler());
    } else {
      this.threadPool = new ThreadPoolExecutor(poolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, queue,
          new DefaultRejectedExecutionHandler());
    }

    completionService = new ExecutorCompletionService<T>(threadPool);
  }

  /**
   * Initialization method for the properties taken from the configuration file
   */
  public void loadConfig() {
    try {
      this.config = new Properties();
      this.config.load(new FileInputStream(CONFIG_FILE));
      this.poolSize = Integer.parseInt(this.config.getProperty("poolSize"));
      this.maxPoolSize = Integer.parseInt(this.config.getProperty("maxPoolSize"));
      this.keepAliveTime = Integer.parseInt(this.config.getProperty("keepAliveTime"));
    } catch (Exception e) {
      log.error("Error when reading the configuration file", e);
      throw new RuntimeException(e);
    }
  }

  /**
   * This method runs the synchronization for all the agent of the same type,
   * creates a thread that will synchronized # of agents (defined by the agentsPerThread field)
   * 
   * @param agentTypeCode
   * @return
   */
  public int run() {
    // localTasksCount contains the number of threads created, this count will be used for shutting down the service
    int localTasksCount = 0;
    // global timer
    this.stopWatch.start();
    /**
     * Timer to measure the total time of execution
     */
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    if (threadPool == null) {// initialize the threadpool if required
      initThreadPool();
    }
    try {
      // gets the agents with service type: this.serviceTypeCode
      List<? extends Callable<T>> jobs = this.createJobList();
      log.debug("# of Jobs to run: {}", jobs.size());
      for (Callable<T> job : jobs) {
        try {
          completionService.submit(job);
          localTasksCount += 1;
        } catch (Exception e) {
          log.error("Error when submiting job: {}", localTasksCount);
        }
      }
      log.debug("{} Jobs submitted succesfully!", localTasksCount);
      this.shutdownService(localTasksCount);
    } catch (Exception e) {
      log.error("Error when submiting jobs: {}", e);
    }
    stopWatch.stop();
    log.debug(String.format("Time running and finishing Jobs: %s", stopWatch.getTime()));
    return localTasksCount;
  }

  public void setConfig(Properties config) {
    this.config = config;
  }

  /**
   * Performs the shutdown activities: waits each thread to complete the process and collects the results; finally send
   * a email notifying the end of the synchronization process
   * 
   * @param tasksCount
   */
  protected void shutdownService(int tasksCount) {
    try {
      log.info("Initiating shutting down of the service");
      List<T> results = new ArrayList<T>();
      for (int i = 0; i < tasksCount; i++) {
        // waits for each task to return the results
        java.util.concurrent.Future<T> result = completionService.take();
        try {
          T taskResponse = result.get();
          results.add(taskResponse);
          log.info("Job result: " + taskResponse.toString());
        } catch (ExecutionException e) {
          log.error("Error waiting a task to return results");
        }
      }
      log.info(String.format("Thread pool status: active count %d, completed task count %d, task count %d",
          this.threadPool.getActiveCount(), this.threadPool.getCompletedTaskCount(), this.threadPool.getTaskCount()));
      List<Runnable> failedTasks = this.threadPool.shutdownNow();
      log.debug("{} Tasks failed or forcibly removed from the thread pool", failedTasks.size());
    } catch (Exception ignored) {
      log.error("Thread pool prematurely terminated", ignored);
    }
    this.stopWatch.stop();
    log.info("Shutdown complete, total time of execution: " + this.stopWatch.toString());
  }
}
