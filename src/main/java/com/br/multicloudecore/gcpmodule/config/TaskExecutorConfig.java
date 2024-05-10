package com.br.multicloudecore.gcpmodule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * The {@code TaskExecutorConfig} class is a configuration class that defines
 * a TaskExecutor bean for creating and managing a thread pool
 * for executing tasks.
 * <p>
 * The TaskExecutor bean returned by this class has the following properties:
 * - Core pool size: 10
 * - Max pool size: 20
 * - Queue capacity: 100
 * - Thread name prefix: MyThread-
 * </p>
 */
@Configuration
public class TaskExecutorConfig {

  /**
   * Returns a TaskExecutor bean that creates and manages a thread pool for executing tasks.
   *
   * @return a TaskExecutor bean
   */
  @Bean
  @Primary
  public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.setMaxPoolSize(20);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("MyThread-");
    executor.initialize();
    return executor;
  }
}
