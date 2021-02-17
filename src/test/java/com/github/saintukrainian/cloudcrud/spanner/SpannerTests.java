package com.github.saintukrainian.cloudcrud.spanner;

import com.github.saintukrainian.cloudcrud.entities.Person;
import com.github.saintukrainian.cloudcrud.entities.PersonDetails;
import com.github.saintukrainian.cloudcrud.entities.PersonWithDetails;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SpannerTests extends AbstractTest {

    @BeforeEach
    public void init() {
        setUp();
    }


    @Test
    public void getPersonsList() throws Exception {
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders.get(PERSONS_URL).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Person[] persons = super.mapFromJson(content, Person[].class);
        Assertions.assertNotNull(persons);
    }

    @Test
    public void getPersonById() throws Exception {
        int id = 1;
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders.get(PERSONS_URL + id).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Person person = super.mapFromJson(content, Person.class);
        assertEquals(person.getId(), id);
        assertEquals(person.getFirstName(), "Denys");
        assertEquals(person.getLastName(), "Matsenko");
        assertEquals(person.getEmail(), "idanchik47@gmail.com");
    }

    @Test
    public void getPersonWithDetailsById() throws Exception {
        int userId = 1;
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders.get(PWD_URL + userId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        PersonWithDetails pwd = super.mapFromJson(content, PersonWithDetails.class);
        assertEquals(pwd.getUserId(), userId);
        assertEquals(pwd.getDetailsId(), userId);
        assertEquals(pwd.getAddress(), "some address");
        assertEquals(pwd.getPhoneNumber(), "4445435465");
    }

    @Test
    public void getPersonDetailsById() throws Exception {
        int userId = 1;
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders.get(PD_URL + userId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        PersonDetails pd = super.mapFromJson(content, PersonDetails.class);
        assertEquals(pd.getUserId(), userId);
        assertEquals(pd.getDetailsId(), userId);
        assertEquals(pd.getAddress(), "some address");
        assertEquals(pd.getPhoneNumber(), "4445435465");
    }

    @Nested
    @SpringBootTest
    public class CrudPersonTest extends AbstractTest {

        @BeforeEach
        public void init() {
            setUp();
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

            int status = mvcResult.getResponse().getStatus();
            String content = mvcResult.getResponse().getContentAsString();

            assertEquals(201, status);
            assertEquals(content, "\"CREATED\"");
        }

        @Test
        public void addPersonDetails() throws Exception {
            PersonDetails personDetails = new PersonDetails();
            personDetails.setUserId(personService.findLatestPersonEntry().getId());
            personDetails.setAddress("some address");
            personDetails.setPhoneNumber("45894365846");

            String inputJson = super.mapToJson(personDetails);
            MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(PD_URL)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inputJson))
                    .andReturn();

            int status = mvcResult.getResponse().getStatus();
            String response = mvcResult.getResponse().getContentAsString();
            assertEquals(201, status);
            assertEquals("\"CREATED\"", response);
        }

        @Test
        public void updatePersonDetails() throws Exception {
            PersonDetails personDetails = new PersonDetails();
            personDetails.setAddress("new address");
            personDetails.setPhoneNumber("45894365846");
            int id = personService.findLatestPersonDetailsEntry().getDetailsId();

            String inputJson = super.mapToJson(personDetails);
            MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(PD_URL + id)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inputJson))
                    .andReturn();

            int status = mvcResult.getResponse().getStatus();
            String response = mvcResult.getResponse().getContentAsString();
            assertEquals(202, status);
            assertEquals("\"ACCEPTED\"", response);
        }

        @Test
        public void updatePerson() throws Exception {
            int userId = personService.findLatestPersonEntry().getId();
            Person person = new Person();
            person.setFirstName("Test");
            person.setLastName("NewTest");
            person.setEmail("test@gmail.com");

            String inputJson = super.mapToJson(person);
            MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(PERSONS_URL + userId)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inputJson))
                    .andReturn();

            int status = mvcResult.getResponse().getStatus();
            String content = mvcResult.getResponse().getContentAsString();
            assertEquals(202, status);
            assertEquals(content, "\"ACCEPTED\"");
        }

        @Test
        public void deletePersonAndDetailsIfExist() throws Exception {
            MvcResult mvcResult;

            Person person = personService.findLatestPersonEntry();
            mvcResult = mvc.perform(MockMvcRequestBuilders.delete(PERSONS_URL + person.getId()))
                    .andReturn();

            assertEquals(200, mvcResult.getResponse().getStatus());
            assertEquals("\"OK\"", mvcResult.getResponse().getContentAsString());
        }
    }

}
