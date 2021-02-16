package com.github.saintukrainian.cloudcrud.entities;

import java.util.List;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class PersonWithPosts {
    private int id;

    private String firstName;

    private String lastName;

    private String email;

    private List<Post> posts;

    public void setFieldsWithPersonInfo(Person person) {
        this.id = person.getId();
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.email = person.getEmail();
    }
}
