package com.github.saintukrainian.bigqueryservice.configs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.Dataset;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
public class BigQueryAuthentication {

  private static BigQuery bigQuery;

  private BigQueryAuthentication() {
    throw new AssertionError();
  }

  public static BigQuery getBigQuery() {
    return bigQuery;
  }

  public static void explicit() throws IOException {
    String projectId = "cloud-crud-304511";
    File credentialsPath = new File("src/main/resources/cloud-crud-304511-1613e3453d69.json");

    // Load credentials from JSON key file. If you can't set the GOOGLE_APPLICATION_CREDENTIALS
    // environment variable, you can explicitly load the credentials file to construct the
    // credentials.
    GoogleCredentials credentials;
    try (FileInputStream serviceAccountStream = new FileInputStream(credentialsPath)) {
      credentials = ServiceAccountCredentials.fromStream(serviceAccountStream);
    }

    // Instantiate a client.
    bigQuery =
        BigQueryOptions.newBuilder()
            .setCredentials(credentials)
            .setProjectId(projectId)
            .build()
            .getService();

    // Use the client.
    log.info("Datasets:");
    for (Dataset dataset : bigQuery.listDatasets().iterateAll()) {
      log.info("{}\n", dataset.getDatasetId().getDataset());
    }
  }

}
