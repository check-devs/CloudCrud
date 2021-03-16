package com.github.saintukrainian.bigqueryservice.entities;

import lombok.Data;

@Data
public class Hospital {
  private String providerId;
  private String hospitalName;
  private String city;
  private String state;
}
