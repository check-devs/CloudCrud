package com.github.saintukrainian.cloudcrud;

import com.github.saintukrainian.cloudcrud.spannerconfig.DockerSpannerConfig;
import com.github.saintukrainian.cloudcrud.spannerconfig.SpannerRemoteConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.Executor;

/** The {@code CloudCrudApplication} class is a driver class for the CloudCrud application */
@SpringBootApplication
@EnableAsync
@EnableEurekaClient
@EnableFeignClients
@RequiredArgsConstructor
@Slf4j
public class CloudCrudApplication {

  private static final DockerSpannerConfig dockerSpannerConfig;
  private final SpannerRemoteConfig spannerRemoteConfig;

  static {
    dockerSpannerConfig = new DockerSpannerConfig();
  }

  @PostConstruct
  public void initEmulator() throws InterruptedException {
    //    dockerSpannerConfig.setupDocker();

    dockerSpannerConfig.setupSpanner(
        spannerRemoteConfig.getProjectId(),
        spannerRemoteConfig.getInstanceId(),
        spannerRemoteConfig.getConfigId());

    dockerSpannerConfig.setupDatabase(
        spannerRemoteConfig.getProjectId(),
        spannerRemoteConfig.getInstanceId(),
        spannerRemoteConfig.getDatabaseName());

    dockerSpannerConfig.fillDatabase();
  }

  @PreDestroy
  public void cleanupEmulator() {
    dockerSpannerConfig.closeSpanner();
    dockerSpannerConfig.stopDocker();
  }

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
