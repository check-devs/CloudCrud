package com.github.saintukrainian.cloudcrud.spanner;

import com.github.saintukrainian.cloudcrud.entities.Person;
import com.github.saintukrainian.cloudcrud.entities.PersonDetails;
import com.github.saintukrainian.cloudcrud.entities.PersonWithDetails;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

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
        Assertions.assertTrue(persons.length > 0);
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
    }

    @Test
    public void getPersonWithDetailsById() throws Exception {
        int userId = 1;
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders.get(PWD_URL + userId).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        PersonWithDetails pwd = super.mapFromJson(content, PersonWithDetails.class);
        Assertions.assertTrue(pwd.getUserId() == userId && pwd.getDetailsId() == userId);
    }

    @Test
    public void getPersonDetailsById() throws Exception {
        int userId = personService.findLatestPersonDetailsEntry().getUserId();
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders.get(PD_URL + userId).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        PersonDetails pd = super.mapFromJson(content, PersonDetails.class);
        Assertions.assertTrue(pd.getDetailsId() == userId && pd.getUserId() == userId);
    }

    @Nested
    @SpringBootTest
    public class CrudPersonTest extends AbstractTest {

        @BeforeEach
        public void init() {
            setUp();
        }

        @Test
        @Transactional
        public void addPerson() throws Exception {
            Person person = new Person();
            person.setFirstName("Test");
            person.setLastName("Test");
            person.setEmail("test@gmail.com");

            String inputJson = super.mapToJson(person);
            MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(PERSONS_URL)
                    .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

            int status = mvcResult.getResponse().getStatus();
            String content = mvcResult.getResponse().getContentAsString();

            assertEquals(201, status);
            assertEquals(content, "\"CREATED\"");
        }

        @Test
        @Transactional
        public void addPersonDetails() throws Exception {
            PersonDetails personDetails = new PersonDetails();
            personDetails.setUserId(personService.findLatestPersonEntry().getId());
            personDetails.setAddress("some address");
            personDetails.setPhoneNumber("45894365846");

            String inputJson = super.mapToJson(personDetails);
            MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(PD_URL)
                    .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

            int status = mvcResult.getResponse().getStatus();
            String response = mvcResult.getResponse().getContentAsString();
            assertEquals(201, status);
            assertEquals("\"CREATED\"", response);
        }

        @Test
        @Transactional
        public void updatePersonDetails() throws Exception {
            PersonDetails personDetails = new PersonDetails();
            personDetails.setAddress("new address");
            personDetails.setPhoneNumber("45894365846");
            int id = personService.findLatestPersonDetailsEntry().getDetailsId();

            String inputJson = super.mapToJson(personDetails);
            MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(PD_URL + id)
                    .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

            int status = mvcResult.getResponse().getStatus();
            String response = mvcResult.getResponse().getContentAsString();
            assertEquals(202, status);
            assertEquals("\"ACCEPTED\"", response);
        }

        @Test
        @Transactional
        public void updatePerson() throws Exception {
            int userId = personService.findLatestPersonEntry().getId();
            Person person = new Person();
            person.setFirstName("Test");
            person.setLastName("NewTest");
            person.setEmail("test@gmail.com");

            String inputJson = super.mapToJson(person);
            MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(PERSONS_URL + userId)
                    .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

            int status = mvcResult.getResponse().getStatus();
            String content = mvcResult.getResponse().getContentAsString();
            assertEquals(202, status);
            assertEquals(content, "\"ACCEPTED\"");
        }

        @Test
        @Transactional
        public void deletePersonAndDetailsIfExist() throws Exception {
            int userId;
            MvcResult mvcResult;

            Person person = personService.findLatestPersonEntry();
            if(person.getFirstName().equals("Test")) {
                userId = person.getId();
            } else {
                personService.savePerson(new Person("jlkhhjkhjkhjkh", "Test", "test@gmail.com"));
                userId = personService.findLatestPersonEntry().getId();
            }

            mvcResult = mvc.perform(MockMvcRequestBuilders.delete(PERSONS_URL + userId))
                    .andReturn();

            assertEquals(200, mvcResult.getResponse().getStatus());
            assertEquals("\"OK\"", mvcResult.getResponse().getContentAsString());
        }
    }

}
