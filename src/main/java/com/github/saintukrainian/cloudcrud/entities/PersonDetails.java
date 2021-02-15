package com.github.saintukrainian.cloudcrud.entities;

import com.google.cloud.spring.data.spanner.core.mapping.Column;
import com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey;
import com.google.cloud.spring.data.spanner.core.mapping.Table;

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

    public PersonDetails() {
    }

    public PersonDetails(int detailsId, int userId, String address, String phoneNumber) {
        this.detailsId = detailsId;
        this.userId = userId;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public int getDetailsId() {
        return detailsId;
    }

    public void setDetailsId(int detailsId) {
        this.detailsId = detailsId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    
}
