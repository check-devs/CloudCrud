package com.github.saintukrainian.cloudcrud.spannerconfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties("spanner")
public class SpannerRemoteConfig {
  private String instanceId;

  private String projectId;

  private String databaseName;

  private String configId;
}
