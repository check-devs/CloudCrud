package com.github.saintukrainian.cloudcrud.entities;

import com.google.cloud.spring.data.spanner.core.mapping.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Denys Matsenko
 *     <p>The {@code PersonWithDetails} class is used to return the whole description about
 *     personality
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonWithDetails {

  @Column(name = "id")
  private int userId;

  @Column(name = "details_id")
  private int detailsId;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "email")
  private String email;

  @Column(name = "address")
  private String address;

  @Column(name = "phone_number")
  private String phoneNumber;
}
