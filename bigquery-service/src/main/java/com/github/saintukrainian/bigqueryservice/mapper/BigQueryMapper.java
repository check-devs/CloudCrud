package com.github.saintukrainian.bigqueryservice.mapper;

import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableResult;

import java.util.List;

public interface BigQueryMapper<T> {

  List<T> mapValuesFromRowsToList(TableResult tableResult);
}
