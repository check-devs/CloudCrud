package com.github.saintukrainian.cloudcrud.spanner;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.saintukrainian.cloudcrud.entities.Person;
import com.github.saintukrainian.cloudcrud.entities.PersonDetails;
import com.github.saintukrainian.cloudcrud.entities.PersonWithDetails;
import com.github.saintukrainian.cloudcrud.entities.PersonWithPosts;
import com.github.saintukrainian.cloudcrud.entities.Post;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
public class SpannerTests extends AbstractTest {

    private static String PERSONS_URL = "http://localhost:8080/persons/";
    private static String PWP_URL = "http://localhost:8080/pwp/";
    private static String POSTS_URL = "http://localhost:8080/posts/";
    private static String PWD_URL = "http://localhost:8080/pwd/";
    private static String PD_URL = "http://localhost:8080/pd/";

    @BeforeEach
    public void init() {
        setUp();
    }


    @Test
    public void getPersonsList() throws Exception {
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders.get(PERSONS_URL).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Person[] persons = super.mapFromJson(content, Person[].class);
        assertTrue(persons.length > 0);
    }

    @Test
    public void getById() throws Exception {
        int id = 1;
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders.get(PERSONS_URL + id).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Person person = super.mapFromJson(content, Person.class);
        Assert.assertEquals(person.getId(), id);
    }

    @Test
    public void addPerson() throws Exception {
        Person person = new Person();
        person.setFirstName("Test");
        person.setLastName("Test");
        person.setEmail("test@gmail.com");

        String inputJson = super.mapToJson(person);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(PERSONS_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "Accepted");
    }

    @Test
    public void updatePerson() throws Exception {
        Person person = new Person();
        person.setId(2);
        person.setFirstName("Test");
        person.setLastName("Test");
        person.setEmail("test@gmail.com");

        String inputJson = super.mapToJson(person);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(PERSONS_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "Accepted");
    }

    @Test
    public void deletePerson() throws Exception {
        int id = 2;
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders.delete(PERSONS_URL + id).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "Gone");
    }

    @Test
    public void getPersonWithPostsByUserId() throws Exception {
        int userId = 1;
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders.get(PWP_URL + userId).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        PersonWithPosts pwp = super.mapFromJson(content, PersonWithPosts.class);
        assertTrue(pwp.getId() == userId && pwp.getPosts().get(0).getId() == userId);
    }

    @Test
    public void getPostsList() throws Exception {
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders.get(POSTS_URL).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Post[] posts = super.mapFromJson(content, Post[].class);
        assertTrue(posts.length > 0);
    }

    @Test
    public void getByUserId() throws Exception {
        int userId = 1;
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders.get(POSTS_URL + userId).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Post[] posts = super.mapFromJson(content, Post[].class);
        assertTrue(posts[0].getUserId() == userId);
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
        assertTrue(pwd.getUserId() == userId && pwd.getDetailsId() == userId);
    }

    @Test
    public void getPersonDetailsById() throws Exception {
        int userId = 1;
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders.get(PD_URL + userId).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        PersonDetails pd = super.mapFromJson(content, PersonDetails.class);
        assertTrue(pd.getDetailsId() == userId && pd.getUserId() == userId);
    }

}
