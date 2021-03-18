package com.github.saintukrainian.bigqueryservice.service;

import com.github.saintukrainian.bigqueryservice.configs.BigQueryAuthentication;
import com.github.saintukrainian.bigqueryservice.entities.Hospital;
import com.github.saintukrainian.bigqueryservice.mapper.BigQueryMapper;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Class for querying data from BigQuery
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class HospitalService extends BigQueryService {

  @Qualifier("hospitalMapper")
  private final BigQueryMapper<Hospital> bigQueryMapper;

  @Value("${sql.hospitals.limit-10}")
  private String HOSPITALS_MAX_10;

  /**
   * Getting hospitals
   * @return list of hospitals
   * @throws InterruptedException is thrown by library
   */
  public List<Hospital> getHospitals() throws InterruptedException {
    QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(HOSPITALS_MAX_10).build();
    Job queryJob = getConfiguredJob(queryConfig);
    queryJob = queryJob.waitFor();
    throwExceptionIfJobIsNull(queryJob);
    TableResult result = queryJob.getQueryResults();

    return bigQueryMapper.mapValuesFromRowsToList(result);
  }
}
