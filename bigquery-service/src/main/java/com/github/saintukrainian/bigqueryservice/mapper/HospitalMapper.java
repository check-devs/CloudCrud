package com.github.saintukrainian.bigqueryservice.mapper;

import com.github.saintukrainian.bigqueryservice.entities.Hospital;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class HospitalMapper implements BigQueryMapper<Hospital>{
  @Override
  public List<Hospital> mapValuesFromRowsToList(TableResult tableResult) {
    List<Hospital> hospitals = new ArrayList<>();
    for (FieldValueList row : tableResult.iterateAll()) {
      Hospital hospital =
          Hospital.builder()
              .providerId(row.get("provider_id").getStringValue())
              .city(row.get("city").getStringValue())
              .state(row.get("state").getStringValue())
              .hospitalName(row.get("hospital_name").getStringValue())
              .build();
      hospitals.add(hospital);
      log.info("Mapped entity: " + hospital.toString());
    }
    return hospitals;
  }
}
