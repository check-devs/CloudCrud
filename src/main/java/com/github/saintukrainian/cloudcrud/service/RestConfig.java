package com.github.saintukrainian.cloudcrud.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author Denys Matsenko
 * <p>
 * Configuration class for {@code RestTemplate} bean
 */
@Configuration
public class RestConfig {

    /**
     * Bean configuration method
     *
     * @return {@code RestTemplate} object
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
