package com.github.saintukrainian.cloudcrud.spanner;

import com.github.saintukrainian.cloudcrud.entities.*;
import com.github.saintukrainian.cloudcrud.repositories.PeronsDetailsRepository;
import com.github.saintukrainian.cloudcrud.repositories.PersonRepository;
import com.github.saintukrainian.cloudcrud.service.PersonService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class SpannerTests extends AbstractTest {

    private static final String PERSONS_URL = "http://localhost:8080/persons/";
    private static final String PWD_URL = "http://localhost:8080/pwd/";
    private static final String PD_URL = "http://localhost:8080/pd/";

    @Autowired
    PersonRepository personRepository;
    @Autowired
    PeronsDetailsRepository peronsDetailsRepository;
    @Autowired
    PersonService personService;

    @BeforeEach
    public void init() {
        setUp();
    }


    @Test
    @Order(value = 1)
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
    @Order(value = 2)
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
    @Order(value = 3)
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

        assertEquals(200, status);
        assertEquals(content, "Accepted");
    }

    @Test
    @Order(value = 4)
    public void addPersonDetails() throws Exception {
        PersonDetails personDetails = new PersonDetails();
        personDetails.setDetailsId(0);
        personDetails.setUserId(personService.findLatestPersonEntry().getId());
        personDetails.setAddress("some address");
        personDetails.setPhoneNumber("45894365846");

        String inputJson = super.mapToJson(personDetails);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(PD_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(200, status);
        assertEquals("Details added", response);
    }

    @Test
    @Order(value = 5)
    public void updatePersonDetails() throws Exception {
        PersonDetails personDetails = new PersonDetails();
        personDetails.setDetailsId(personService.findLatestPersonDetailsEntry().getDetailsId());
        personDetails.setUserId(personService.findLatestPersonDetailsEntry().getUserId());
        personDetails.setAddress("new address");
        personDetails.setPhoneNumber("45894365846");

        String inputJson = super.mapToJson(personDetails);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(PD_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(200, status);
        assertEquals("Details updated", response);
    }

    @Test
    @Order(value = 6)
    public void updatePerson() throws Exception {
        int userId = personService.findLatestPersonEntry().getId();
        Person person = new Person();
        person.setId(userId);
        person.setFirstName("NewName");
        person.setLastName("New");
        person.setEmail("test@gmail.com");

        String inputJson = super.mapToJson(person);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(PERSONS_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(200, status);
        assertEquals(content, "Accepted");
    }

    @Test
    @Order(value = 7)
    public void deletePersonAndDetailsIfExist() throws Exception {
        int userId;
        MvcResult mvcResult;

        userId = personService.findLatestPersonEntry().getId();
        mvcResult = mvc.perform(MockMvcRequestBuilders.delete(PERSONS_URL + userId))
                .andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
        assertEquals("Gone", mvcResult.getResponse().getContentAsString());
    }

    @Test
    @Order(value = 11)
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
    @Order(value = 12)
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

}
