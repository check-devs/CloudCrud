package com.github.saintukrainian.bigqueryservice.service;

import com.github.saintukrainian.bigqueryservice.entities.LondonCrime;
import com.google.cloud.bigquery.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class LondonCrimeService extends BigQueryService {

  @Value("${sql.london-crimes.by.year.limit-10}")
  private String LONDON_CRIMES_MAX_10;

  @Value("${sql.london-crimes.by.popularity.year.limit-1}")
  private String LONDON_CRIME_MOST_POPULAR;

  public List<LondonCrime> getLondonCrimesByYear(int year) throws InterruptedException {
    List<LondonCrime> crimesByYear = new ArrayList<>();
    QueryJobConfiguration queryConfig =
        QueryJobConfiguration.newBuilder(LONDON_CRIMES_MAX_10)
            .addNamedParameter("year", QueryParameterValue.int64(year))
            .setUseLegacySql(false)
            .build();

    // Create a job ID so that we can safely retry.
    Job queryJob = getConfiguredJob(queryConfig);

    queryJob = queryJob.waitFor();

    if (queryJob == null) {
      throw new RuntimeException("Job no longer exists");
    } else if (queryJob.getStatus().getError() != null) {
      throw new RuntimeException(queryJob.getStatus().getError().toString());
    }
    TableResult result = queryJob.getQueryResults();

    for (FieldValueList row : result.iterateAll()) {
      LondonCrime londonCrime =
          LondonCrime.builder()
              .borough(row.get("borough").getStringValue())
              .lsoaCode(row.get("lsoa_code").getStringValue())
              .year((int) row.get("year").getLongValue())
              .minorCategoryName(row.get("minor_category").getStringValue())
              .majorCategoryName(row.get("major_category").getStringValue())
              .value((int) row.get("value").getLongValue())
              .totalNumberOfMinorCrime(
                  row.get("total_number_of_crime_by_minor_category").getLongValue())
              .totalNumberOfMajorCrime(
                  row.get("total_number_of_crime_by_major_category").getLongValue())
              .build();
      crimesByYear.add(londonCrime);
      log.info(londonCrime.toString());
    }

    return crimesByYear;
  }

  public LondonCrime getTheMostCriminal(int year) throws InterruptedException {
    QueryJobConfiguration queryConfig =
        QueryJobConfiguration.newBuilder(LONDON_CRIME_MOST_POPULAR)
            .addNamedParameter("year", QueryParameterValue.int64(year))
            .setUseLegacySql(false)
            .build();

    // Create a job ID so that we can safely retry.
    Job queryJob = getConfiguredJob(queryConfig);

    queryJob = queryJob.waitFor();

    if (queryJob == null) {
      throw new RuntimeException("Job no longer exists");
    } else if (queryJob.getStatus().getError() != null) {
      throw new RuntimeException(queryJob.getStatus().getError().toString());
    }
    TableResult result = queryJob.getQueryResults();

    LondonCrime londonCrime = null;
    for (FieldValueList row : result.iterateAll()) {
      londonCrime =
          LondonCrime.builder()
              .borough(row.get("borough").getStringValue())
              .lsoaCode(row.get("lsoa_code").getStringValue())
              .year((int) row.get("year").getLongValue())
              .minorCategoryName(row.get("minor_category").getStringValue())
              .majorCategoryName(row.get("major_category").getStringValue())
              .value((int) row.get("value").getLongValue())
              .totalNumberOfMinorCrime(
                  row.get("total_number_of_crime_by_minor_category").getLongValue())
              .totalNumberOfMajorCrime(
                  row.get("total_number_of_crime_by_major_category").getLongValue())
              .build();
      log.info(londonCrime.toString());
    }
    return londonCrime;
  }
}
