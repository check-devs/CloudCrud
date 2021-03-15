package com.github.saintukrainian.cloudcrud;

import com.github.saintukrainian.cloudcrud.spannerconfig.DockerSpannerConfig;
import com.github.saintukrainian.cloudcrud.spannerconfig.SpannerRemoteConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@TestConfiguration
@RequiredArgsConstructor
public class CloudCrudApplicationTestContext {

  private final DockerSpannerConfig dockerSpannerConfig;
  private final SpannerRemoteConfig spannerRemoteConfig;

  @PostConstruct
  public void contextLoads() throws InterruptedException {
    System.out.println(spannerRemoteConfig.toString());
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
