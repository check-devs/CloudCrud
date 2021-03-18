package com.github.saintukrainian.bigqueryservice;

//import com.google.cloud.bigquery.BigQuery;
//import com.google.cloud.bigquery.BigQueryOptions;
import com.github.saintukrainian.bigqueryservice.configs.BigQueryAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Slf4j
@SpringBootApplication
@EnableEurekaClient
public class BigqueryServiceApplication {

	@PostConstruct
	public void initBigQuery() throws IOException {
		BigQueryAuthentication.explicit();
	}

	public static void main(String[] args) {
		SpringApplication.run(BigqueryServiceApplication.class, args);
	}

}
