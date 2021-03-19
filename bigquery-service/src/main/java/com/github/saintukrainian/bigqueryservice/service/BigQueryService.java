package com.github.saintukrainian.bigqueryservice.service;

import com.github.saintukrainian.bigqueryservice.configs.BigQueryAuthentication;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.QueryJobConfiguration;
import org.springframework.context.annotation.PropertySource;

import java.util.UUID;

@PropertySource("classpath:sql.properties")
public class BigQueryService {

  protected JobId getRandomJobId() {
    return JobId.of(UUID.randomUUID().toString());
  }

  protected Job getConfiguredJob(QueryJobConfiguration queryJobConfiguration) {
    return BigQueryAuthentication.getBigQuery()
        .create(JobInfo.newBuilder(queryJobConfiguration).setJobId(getRandomJobId()).build());
  }

  protected void throwExceptionIfJobIsNull(Job queryJob) {
    if (queryJob == null) {
      throw new RuntimeException("Job no longer exists");
    } else if (queryJob.getStatus().getError() != null) {
      throw new RuntimeException(queryJob.getStatus().getError().toString());
    }
  }
}
