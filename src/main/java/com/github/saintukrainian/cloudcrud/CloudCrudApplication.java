package com.github.saintukrainian.cloudcrud;

import com.github.saintukrainian.cloudcrud.spannerconfig.DockerSpannerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/** The {@code CloudCrudApplication} class is a driver class for the CloudCrud application */
@SpringBootApplication
@EnableAsync
public class CloudCrudApplication {

  private static final DockerSpannerConfig dockerSpannerConfig;

  static {
    dockerSpannerConfig = new DockerSpannerConfig();
  }

  //	@PostConstruct
  //	public void initEmulator() throws InterruptedException {
  //		dockerSpannerConfig.setupDocker();
  //		dockerSpannerConfig.setupSpanner();
  //		dockerSpannerConfig.setupDatabase();
  //		dockerSpannerConfig.fillDatabase();
  //	}
  //
  //	@PreDestroy
  //	public void cleanupEmulator() {
  //		dockerSpannerConfig.closeSpanner();
  //		dockerSpannerConfig.stopDocker();
  //	}

  @Bean
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(3);
    executor.setMaxPoolSize(6);
    executor.setQueueCapacity(3);
    executor.setThreadNamePrefix("CloudCrud-");
    executor.initialize();
    return executor;
  }

  public static void main(String[] args) {
    SpringApplication.run(CloudCrudApplication.class, args);
  }
}
