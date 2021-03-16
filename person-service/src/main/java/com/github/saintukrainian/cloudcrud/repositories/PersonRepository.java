package com.github.saintukrainian.cloudcrud.repositories;

import com.github.saintukrainian.cloudcrud.entities.Person;
import com.google.cloud.spring.data.spanner.repository.SpannerRepository;

import org.springframework.stereotype.Repository;

/**
 * @author Denys Matsenko
 *     <p>The {@code PersonRepository} interface is a simple spanner repository for {@code Person}
 *     entity class
 */
@Repository
public interface PersonRepository extends SpannerRepository<Person, Integer> {}
