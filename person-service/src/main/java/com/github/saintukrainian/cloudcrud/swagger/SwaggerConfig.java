package com.github.saintukrainian.cloudcrud.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/** Swagger configuration class */
@EnableSwagger2
@Configuration
public class SwaggerConfig {

  /**
   * Bean configuration
   *
   * @return swagger config
   */
  @Bean
  public Docket productApi() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(
            RequestHandlerSelectors.basePackage(
                "com.github.saintukrainian.cloudcrud.restcontrollers"))
        .build();
  }
}
