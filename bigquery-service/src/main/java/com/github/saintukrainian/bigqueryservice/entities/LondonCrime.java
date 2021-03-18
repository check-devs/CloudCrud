package com.github.saintukrainian.bigqueryservice.entities;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class LondonCrime {

  private Integer year;
  private String minorCategoryName;
  private String majorCategoryName;
  private String borough;
  private Integer month;
  private Integer value;
  private String lsoaCode;
  private Long totalNumberOfMinorCrime;
  private Long totalNumberOfMajorCrime;

}
