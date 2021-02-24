package com.github.saintukrainian.cloudcrud;

import com.github.saintukrainian.cloudcrud.spannerconfig.DockerSpannerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@SpringBootApplication
public class CloudcrudApplication {

	private static final DockerSpannerConfig dockerSpannerConfig;

	static {
		dockerSpannerConfig = new DockerSpannerConfig();
	}

	@PostConstruct
	public void initEmulator() throws InterruptedException {
		dockerSpannerConfig.setupDocker();
		dockerSpannerConfig.setupSpanner();
		dockerSpannerConfig.setupDatabase();
		dockerSpannerConfig.fillDatabase();
	}

	@PreDestroy
	public void cleanupEmulator() {
		dockerSpannerConfig.closeSpanner();
		dockerSpannerConfig.stopDocker();
	}


	public static void main(String[] args) {
		SpringApplication.run(CloudcrudApplication.class, args);
	}
}
