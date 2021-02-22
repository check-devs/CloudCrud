package com.github.saintukrainian.cloud.repositories;

import com.github.saintukrainian.cloud.entities.Person;
import com.google.cloud.spring.data.spanner.repository.SpannerRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends SpannerRepository<Person, Integer> {
    
}
