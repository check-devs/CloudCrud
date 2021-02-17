package com.github.saintukrainian.cloudcrud.personbyparams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.saintukrainian.cloudcrud.entities.Person;

import com.github.saintukrainian.cloudcrud.entities.SearchParams;
import com.github.saintukrainian.cloudcrud.spanner.AbstractTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class PersonByParamsTest extends AbstractTest {

    private static final String PERSONS_URL = "http://localhost:8080/persons/";

    @BeforeEach
    public void init() {
        setUp();
    }

    @Test
    public void getPersonByQueryParamFirstName() throws Exception {
        SearchParams params = new SearchParams("Denys");
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders.post(PERSONS_URL + "search")
                        .content(mapToJson(params))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Person[] persons = super.mapFromJson(content, Person[].class);
        assertEquals(params.getFirstName(), persons[0].getFirstName());
    }
    
}
