package com.github.saintukrainian.bigqueryservice.restcontroller;

import com.github.saintukrainian.bigqueryservice.configs.BigQueryAuthentication;
import com.github.saintukrainian.bigqueryservice.entities.Hospital;
import com.google.cloud.bigquery.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@PropertySource("classpath:sql.properties")
@RestController
public class DatasetController {

  @Value("${sql.hospitals.limit-10}")
  private String HOSPITALS_MAX_10;



  @GetMapping("/hospitals")
  public List<Hospital> test() throws InterruptedException {
    QueryJobConfiguration queryConfig =
        QueryJobConfiguration.newBuilder(HOSPITALS_MAX_10)
            .setUseLegacySql(false)
            .build();

    // Create a job ID so that we can safely retry.
    JobId jobId = JobId.of(UUID.randomUUID().toString());
    Job queryJob =
        BigQueryAuthentication.getBigQuery()
            .create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

    queryJob = queryJob.waitFor();

    if (queryJob == null) {
      throw new RuntimeException("Job no longer exists");
    } else if (queryJob.getStatus().getError() != null) {
      throw new RuntimeException(queryJob.getStatus().getError().toString());
    }
    TableResult result = queryJob.getQueryResults();

    List<Hospital> simpleEntities = new ArrayList<>();
    for (FieldValueList row : result.iterateAll()) {
      Hospital hospital = new Hospital();
      hospital.setProviderId(row.get("provider_id").getStringValue());
      hospital.setCity(row.get("city").getStringValue());
      hospital.setHospitalName(row.get("hospital_name").getStringValue());
      hospital.setState(row.get("state").getStringValue());
      simpleEntities.add(hospital);
      log.info(hospital.toString());
    }
    return simpleEntities;
  }
}
