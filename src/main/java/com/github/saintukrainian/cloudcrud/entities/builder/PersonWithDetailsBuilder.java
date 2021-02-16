package com.github.saintukrainian.cloudcrud.entities.builder;

import com.github.saintukrainian.cloudcrud.entities.PersonWithDetails;

public class PersonWithDetailsBuilder {

    private PersonWithDetails personWithDetails;

    public PersonWithDetailsBuilder() {
        personWithDetails = new PersonWithDetails();
    }

    public PersonWithDetailsBuilder firstName(String firstName) {
        this.personWithDetails.setFirstName(firstName);
        return this;
    }

    public PersonWithDetailsBuilder lastName(String lastName) {
        this.personWithDetails.setLastName(lastName);
        return this;
    }

    public PersonWithDetailsBuilder email(String email) {
        this.personWithDetails.setEmail(email);
        return this;
    }

    public PersonWithDetailsBuilder id(int id) {
        this.personWithDetails.setDetailsId(id);
        this.personWithDetails.setUserId(id);
        return this;
    }

    public PersonWithDetailsBuilder address(String address) {
        this.personWithDetails.setAddress(address);
        return this;
    }

    public PersonWithDetailsBuilder phoneNumber(String phoneNumber) {
        this.personWithDetails.setPhoneNumber(phoneNumber);
        return this;
    }

    public PersonWithDetails build() {
        return this.personWithDetails;
    }
}
