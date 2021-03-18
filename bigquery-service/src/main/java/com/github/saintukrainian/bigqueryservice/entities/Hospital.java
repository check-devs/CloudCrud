package com.github.saintukrainian.bigqueryservice.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Hospital {
  private String providerId;
  private String hospitalName;
  private String city;
  private String state;
}
