package com.github.saintukrainian.bigqueryservice.service;

import com.github.saintukrainian.bigqueryservice.entities.Hospital;
import com.github.saintukrainian.bigqueryservice.mapper.BigQueryMapper;
import com.github.saintukrainian.bigqueryservice.mapper.HospitalMapper;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class HospitalService extends BigQueryService {

  @Qualifier("hospitalMapper")
  private final BigQueryMapper<Hospital> bigQueryMapper;

  @Value("${sql.hospitals.limit-10}")
  private String HOSPITALS_MAX_10;

  public List<Hospital> getHospitals() throws InterruptedException {

    QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(HOSPITALS_MAX_10).build();
    Job queryJob = getConfiguredJob(queryConfig);
    queryJob = queryJob.waitFor();
    throwExceptionIfJobIsNull(queryJob);
    TableResult result = queryJob.getQueryResults();

    return bigQueryMapper.mapValuesFromRowsToList(result);
  }
}
