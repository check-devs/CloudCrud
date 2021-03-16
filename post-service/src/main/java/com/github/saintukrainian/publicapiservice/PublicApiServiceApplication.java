package com.github.saintukrainian.publicapiservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class PublicApiServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(PublicApiServiceApplication.class, args);
  }
}
