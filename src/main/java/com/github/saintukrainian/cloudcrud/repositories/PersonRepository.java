package com.github.saintukrainian.cloudcrud.repositories;

import com.github.saintukrainian.cloudcrud.entities.Person;
import com.google.cloud.spring.data.spanner.repository.SpannerRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends SpannerRepository<Person, Integer> {
    
}
