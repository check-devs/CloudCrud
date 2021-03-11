package com.github.saintukrainian.cloudcrud.spannerconfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("spanner")
public class SpannerRemoteConfig {
  @Getter @Setter private String instanceId;

  @Getter @Setter private String projectId;

  @Getter @Setter private String databaseName;

  @Getter @Setter private String configId;
}
