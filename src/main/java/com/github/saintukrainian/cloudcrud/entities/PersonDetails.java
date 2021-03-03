package com.github.saintukrainian.cloudcrud.entities;

import com.google.cloud.spring.data.spanner.core.mapping.Column;
import com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey;
import com.google.cloud.spring.data.spanner.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Denys Matsenko
 * <p>
 * The {@code PersonDetails} class describes details of person (address, phone number)
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "person_details")
public class PersonDetails {

    @PrimaryKey
    @Column(name = "details_id")
    private int detailsId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

}
