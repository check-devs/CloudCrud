package com.github.saintukrainian.bigqueryservice.configs;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
public class BigQueryAuthentication {

  private static BigQuery bigQuery;

  private BigQueryAuthentication() {
    throw new AssertionError();
  }

  public static BigQuery getBigQuery() {
    if (bigQuery == null) {
      try {
        bigQuery =
            BigQueryOptions.newBuilder()
                .setCredentials(
                    GoogleCredentials.fromStream(
                        new FileInputStream(
                            "src/main/resources/cloud-crud-304511-1613e3453d69.json")))
                .build()
                .getService();
      } catch (IOException e) {
        log.error("Credentials not found!");
        e.printStackTrace();
      }
    }
    return bigQuery;
  }
}
