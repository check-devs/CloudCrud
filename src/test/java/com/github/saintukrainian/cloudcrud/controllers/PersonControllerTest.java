package com.github.saintukrainian.cloudcrud.controllers;

import com.github.saintukrainian.cloudcrud.entities.Person;
import com.github.saintukrainian.cloudcrud.entities.SearchParams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PersonControllerTest extends AbstractTest{

    @BeforeEach
    public void init() {
        setUp();
    }

    @Test
    public void getPersonsList() throws Exception {
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders.get(PERSONS_URL).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();
        assertEquals(200, status);
        String content = response.getContentAsString();
        Person[] persons = super.mapFromJson(content, Person[].class);
        Assertions.assertNotNull(persons);

        Person person1 = new Person(1, "Denys", "Matsenko", "idanchik47@gmail.com");
        Person person2 = new Person(2, "Max", "Basov", "scratchy@gmail.com");
//        Person person3 = new Person(3, "Kirill", "Ikumapaii", "merlodon@gmail.com");

        assertEquals(person1, persons[0]);
        assertEquals(person2, persons[1]);
//        assertEquals(person3, persons[2]);
    }

    @Test
    public void getPersonById() throws Exception {
        int id = 1;
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders.get(PERSONS_URL + id).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();
        assertEquals(200, status);
        String content = response.getContentAsString();
        Person person = super.mapFromJson(content, Person.class);
        Person personToBeEqualTo = new Person(1, "Denys", "Matsenko", "idanchik47@gmail.com");
        assertEquals(person, personToBeEqualTo);
    }


    @Test
    public void addPerson() throws Exception {
        Person person = new Person();
        person.setFirstName("Test");
        person.setLastName("Test");
        person.setEmail("test@gmail.com");

        String inputJson = super.mapToJson(person);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(PERSONS_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson))
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();
        assertEquals(201, status);
        String content = response.getContentAsString();
        assertEquals(content, "\"CREATED\"");
    }

    @Test
    public void updatePerson() throws Exception {
        int userId = personService.getLatestPersonEntry().getId();
        Person person = new Person();
        person.setFirstName("Test");
        person.setLastName("NewTest");
        person.setEmail("test@gmail.com");

        String inputJson = super.mapToJson(person);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(PERSONS_URL + userId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson))
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();
        assertEquals(202, status);
        String content = response.getContentAsString();
        assertEquals(content, "\"ACCEPTED\"");
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
        Person person = new Person(1, "Denys", "Matsenko", "idanchik47@gmail.com");
        assertEquals(person, persons[0]);
    }

    @Test
    public void deletePersonAndDetailsIfExist() throws Exception {
        MvcResult mvcResult;

        Person person = personService.getLatestPersonEntry();
        mvcResult = mvc.perform(MockMvcRequestBuilders.delete(PERSONS_URL + person.getId()))
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(200, response.getStatus());
        assertEquals("\"OK\"", response.getContentAsString());
    }

    @Test
    public void personNotFound() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(PERSONS_URL + 10000)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(404, response.getStatus());
        assertEquals("\"NOT_FOUND\"", response.getContentAsString());
    }

    @Test
    public void personWithDetailsNotFound() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(PWD_URL + 10000)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(404, response.getStatus());
        assertEquals("\"NOT_FOUND\"", response.getContentAsString());
    }

    @Test
    public void personBadRequest() throws Exception {
        Person person = new Person(1, "Denys", "Matsenko", "idanchik47@gmail.com");
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(PERSONS_URL)
                .content(mapToJson(person))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(400, response.getStatus());
        assertEquals("\"BAD_REQUEST\"", response.getContentAsString());
    }

}
