package com.github.saintukrainian.bigqueryservice.mapper;

import com.github.saintukrainian.bigqueryservice.entities.LondonCrime;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class LondonCrimeMapper implements BigQueryMapper<LondonCrime>{

  @Override
  public List<LondonCrime> mapValuesFromRowsToList(TableResult tableResult) {
    List<LondonCrime> londonCrimes = new ArrayList<>();
    for (FieldValueList row : tableResult.iterateAll()) {
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
      londonCrimes.add(londonCrime);
      log.info("Mapped entity: " + londonCrime.toString());
    }
    return londonCrimes;
  }
}
