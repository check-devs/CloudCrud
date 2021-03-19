package com.github.saintukrainian.bigqueryservice.configs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class BigQueryAuthentication {

  private static BigQuery bigQuery;

  private BigQueryAuthentication() {
    throw new AssertionError();
  }

  // in progress
  public static BigQuery getBigQuery() {
    if (bigQuery == null) {
      log.info("Instantiating bigquery...");
      log.info("project id from bigquery options: {}", BigQueryOptions.getDefaultProjectId());
      bigQuery =
          BigQueryOptions.getDefaultInstance()
              .getService();
    }
    return bigQuery;
  }
}
