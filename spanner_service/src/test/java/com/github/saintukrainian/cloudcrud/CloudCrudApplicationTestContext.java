package com.github.saintukrainian.cloudcrud;

import com.github.saintukrainian.cloudcrud.spannerconfig.DockerSpannerConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@TestConfiguration
public class CloudCrudApplicationTestContext {

  private final DockerSpannerConfig dockerSpannerConfig;

  {
    dockerSpannerConfig = new DockerSpannerConfig();
  }

  @PostConstruct
  public void contextLoads() throws InterruptedException {
    dockerSpannerConfig.setupDocker();
    dockerSpannerConfig.setupSpanner();
    dockerSpannerConfig.setupDatabase();
    dockerSpannerConfig.fillDatabase();
  }

  @PreDestroy
  public void contextCloses() {
    dockerSpannerConfig.closeSpanner();
    dockerSpannerConfig.stopDocker();
  }
}
