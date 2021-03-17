package com.github.saintukrainian.bigqueryservice.service;

import com.github.saintukrainian.bigqueryservice.configs.BigQueryAuthentication;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.QueryJobConfiguration;

import java.util.UUID;

public class BigQueryService {

  protected JobId getRandomJobId() {
    return JobId.of(UUID.randomUUID().toString());
  }

  protected Job getConfiguredJob(QueryJobConfiguration queryJobConfiguration) {
    return BigQueryAuthentication.getBigQuery()
        .create(JobInfo.newBuilder(queryJobConfiguration).setJobId(getRandomJobId()).build());
  }
}
