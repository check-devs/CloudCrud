package com.github.saintukrainian.bigqueryservice.service;

import com.github.saintukrainian.bigqueryservice.entities.Hospital;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class HospitalService extends BigQueryService{

  @Value("${sql.hospitals.limit-10}")
  private String HOSPITALS_MAX_10;

  public List<Hospital> getHospitals() throws InterruptedException {

    QueryJobConfiguration queryConfig =
        QueryJobConfiguration.newBuilder(HOSPITALS_MAX_10).setUseLegacySql(false).build();

    // Create a job ID so that we can safely retry.
    Job queryJob = getConfiguredJob(queryConfig);

    queryJob = queryJob.waitFor();

    if (queryJob == null) {
      throw new RuntimeException("Job no longer exists");
    } else if (queryJob.getStatus().getError() != null) {
      throw new RuntimeException(queryJob.getStatus().getError().toString());
    }
    TableResult result = queryJob.getQueryResults();

    List<Hospital> simpleEntities = new ArrayList<>();
    for (FieldValueList row : result.iterateAll()) {
      Hospital hospital =
          Hospital.builder()
              .providerId(row.get("provider_id").getStringValue())
              .city(row.get("city").getStringValue())
              .state(row.get("state").getStringValue())
              .hospitalName(row.get("hospital_name").getStringValue())
              .build();
      simpleEntities.add(hospital);
      log.info(hospital.toString());
    }
    return simpleEntities;

  }
}
