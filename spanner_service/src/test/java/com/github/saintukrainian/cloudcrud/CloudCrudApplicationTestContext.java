package com.github.saintukrainian.cloudcrud;

import com.github.saintukrainian.cloudcrud.spannerconfig.DockerSpannerConfig;
import com.github.saintukrainian.cloudcrud.spannerconfig.SpannerRemoteConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@TestConfiguration
public class CloudCrudApplicationTestContext {

  private final DockerSpannerConfig dockerSpannerConfig;
  private final SpannerRemoteConfig spannerRemoteConfig;

  {
    dockerSpannerConfig = new DockerSpannerConfig();
    spannerRemoteConfig = new SpannerRemoteConfig();
  }

  @PostConstruct
  public void contextLoads() throws InterruptedException {
    dockerSpannerConfig.setupDocker();

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
  public void contextCloses() {
    dockerSpannerConfig.closeSpanner();
    dockerSpannerConfig.stopDocker();
  }
}
