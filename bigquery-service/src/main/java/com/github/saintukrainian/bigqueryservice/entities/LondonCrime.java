package com.github.saintukrainian.bigqueryservice.entities;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class LondonCrime {

  private int year;
  private String minorCategoryName;
  private String majorCategoryName;
  private String borough;
  private int month;
  private int value;
  private String lsoaCode;
  private long totalNumberOfMinorCrime;
  private long totalNumberOfMajorCrime;

}
