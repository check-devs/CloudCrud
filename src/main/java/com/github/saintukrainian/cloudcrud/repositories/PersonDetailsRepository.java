package com.github.saintukrainian.cloudcrud.repositories;

import com.github.saintukrainian.cloudcrud.entities.PersonDetails;
import com.google.cloud.spring.data.spanner.repository.SpannerRepository;

import org.springframework.stereotype.Repository;

/**
 * @author Denys Matsenko
 *     <p>The {@code PersonDetailsRepository} interface is a simple spanner repository for {@code
 *     PersonDetails} entity class
 */
@Repository
public interface PersonDetailsRepository extends SpannerRepository<PersonDetails, Integer> {}
