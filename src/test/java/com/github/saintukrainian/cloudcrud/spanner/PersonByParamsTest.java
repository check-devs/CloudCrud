package com.github.saintukrainian.cloudcrud.spanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.saintukrainian.cloudcrud.entities.Person;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class PersonByParamsTest extends AbstractTest {

    private static String PERSONS_URL = "http://localhost:8080/persons/";

    @BeforeEach
    public void init() {
        setUp();
    }

    @Test
    public void getPersonByQueryParamFirstName() throws Exception {
        String firstName = "Denys";
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders.get(PERSONS_URL + "/search?firstName=" + firstName).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Person[] persons = super.mapFromJson(content, Person[].class);
        assertTrue(persons[0].getFirstName().equals(firstName));
    }
    
}
