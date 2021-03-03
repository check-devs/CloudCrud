package com.github.saintukrainian.cloudcrud.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saintukrainian.cloudcrud.repositories.PersonDetailsRepository;
import com.github.saintukrainian.cloudcrud.repositories.PersonRepository;
import com.github.saintukrainian.cloudcrud.service.PersonService;
import com.github.saintukrainian.cloudcrud.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

@SpringBootTest
@WebAppConfiguration
public abstract class AbstractTest {

    protected MockMvc mvc;
    protected static final String PERSONS_URL = "http://localhost:8080/persons/";
    protected static final String PWD_URL = "http://localhost:8080/pwd/";
    protected static final String PD_URL = "http://localhost:8080/pd/";

    @Autowired
    PersonRepository personRepository;
    @Autowired
    PersonDetailsRepository personDetailsRepository;
    @Autowired
    PersonService personService;
    @Autowired
    PostService postService;

    @Autowired
    WebApplicationContext webApplicationContext;

    protected void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    protected String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    protected <T> T mapFromJson(String json, Class<T> clazz)
            throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clazz);
    }
}
