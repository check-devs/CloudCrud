package com.github.saintukrainian.bigqueryservice.entities;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class Hospital {
  private String providerId;
  private String hospitalName;
  private String city;
  private String state;
}
