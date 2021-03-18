package com.github.saintukrainian.bigqueryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class BigqueryServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(BigqueryServiceApplication.class, args);
  }
}
