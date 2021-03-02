package com.github.saintukrainian.cloudcrud;

import com.github.saintukrainian.cloudcrud.spannerconfig.DockerSpannerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * The {@code CloudcrudApplication} class is a driver class for the CloudCrud application
 */
@SpringBootApplication
@EnableAsync
public class CloudcrudApplication {

	private static final DockerSpannerConfig dockerSpannerConfig;

	static {
		dockerSpannerConfig = new DockerSpannerConfig();
	}

//	@PostConstruct
//	public void initEmulator() throws InterruptedException {
//		dockerSpannerConfig.setupDocker();
//		dockerSpannerConfig.setupSpanner();
//		dockerSpannerConfig.setupDatabase();
//		dockerSpannerConfig.fillDatabase();
//	}
//
//	@PreDestroy
//	public void cleanupEmulator() {
//		dockerSpannerConfig.closeSpanner();
//		dockerSpannerConfig.stopDocker();
//	}
//

	public static void main(String[] args) {
		SpringApplication.run(CloudcrudApplication.class, args);
	}
}
