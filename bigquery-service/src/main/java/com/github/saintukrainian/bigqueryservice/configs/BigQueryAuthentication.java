package com.github.saintukrainian.bigqueryservice.configs;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BigQueryAuthentication {

  private static BigQuery bigQuery;

  private BigQueryAuthentication() {
    throw new AssertionError();
  }

  public static BigQuery getBigQuery() {
    if (bigQuery == null) {
      bigQuery =
          BigQueryOptions.getDefaultInstance().getService();
    }
    return bigQuery;
  }
}
