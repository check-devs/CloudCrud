package com.github.saintukrainian.cloudcrud.personwithinfo;

import com.github.saintukrainian.cloudcrud.repositories.PeronsDetailsRepository;
import com.github.saintukrainian.cloudcrud.repositories.PersonRepository;
import com.github.saintukrainian.cloudcrud.service.PersonService;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@TestConfiguration
public class PersonWithInfoTestConf {

    @Bean
    public PersonService personService(PersonRepository pr, PeronsDetailsRepository pd, SpannerTemplate st) {
        return new PersonService(pr, pd, st);
    }
}
