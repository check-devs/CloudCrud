package com.github.saintukrainian.cloud.repositories;
import com.github.saintukrainian.cloud.entities.PersonDetails;
import com.google.cloud.spring.data.spanner.repository.SpannerRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface PeronsDetailsRepository extends SpannerRepository<PersonDetails, Integer> {
    
}
