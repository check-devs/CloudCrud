package com.github.saintukrainian.bigqueryservice.service;

import com.github.saintukrainian.bigqueryservice.entities.LondonCrime;
import com.github.saintukrainian.bigqueryservice.mapper.BigQueryMapper;
import com.google.cloud.bigquery.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class LondonCrimeService extends BigQueryService {

  @Qualifier("londonCrimeMapper")
  private final BigQueryMapper<LondonCrime> bigQueryMapper;

  @Value("${sql.london-crimes.by.year.limit-10}")
  private String LONDON_CRIMES_MAX_10;

  @Value("${sql.london-crimes.by.popularity.year.limit-1}")
  private String LONDON_CRIME_MOST_POPULAR;

  public List<LondonCrime> getLondonCrimesByYear(int year) throws InterruptedException {
    QueryJobConfiguration queryConfig =
        QueryJobConfiguration.newBuilder(LONDON_CRIMES_MAX_10)
            .addNamedParameter("year", QueryParameterValue.int64(year))
            .build();
    Job queryJob = getConfiguredJob(queryConfig);
    queryJob = queryJob.waitFor();
    throwExceptionIfJobIsNull(queryJob);
    TableResult result = queryJob.getQueryResults();

    return bigQueryMapper.mapValuesFromRowsToList(result);
  }

  public List<LondonCrime> getTheMostCriminalCategoryByYear(int year) throws InterruptedException {
    QueryJobConfiguration queryConfig =
        QueryJobConfiguration.newBuilder(LONDON_CRIME_MOST_POPULAR)
            .addNamedParameter("year", QueryParameterValue.int64(year))
            .build();

    Job queryJob = getConfiguredJob(queryConfig);
    queryJob = queryJob.waitFor();
    throwExceptionIfJobIsNull(queryJob);
    TableResult result = queryJob.getQueryResults();

    return bigQueryMapper.mapValuesFromRowsToList(result);
  }
}
