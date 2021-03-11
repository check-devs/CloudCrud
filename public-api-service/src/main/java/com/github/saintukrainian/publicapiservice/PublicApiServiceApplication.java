package com.github.saintukrainian.publicapiservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@Slf4j
@RequiredArgsConstructor
public class PublicApiServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PublicApiServiceApplication.class, args);
	}

}
