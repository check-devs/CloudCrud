package com.github.saintukrainian.cloudcrud.spanner;

import com.github.saintukrainian.cloudcrud.spannerconfig.DockerSpannerConfig;
import com.github.saintukrainian.cloudcrud.entities.*;
import com.github.saintukrainian.cloudcrud.repositories.PeronsDetailsRepository;
import com.github.saintukrainian.cloudcrud.repositories.PersonRepository;
import com.github.saintukrainian.cloudcrud.service.PersonService;
import com.github.saintukrainian.cloudcrud.service.PostService;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@PropertySource("classpath:docker-java.properties")
public class SpannerTests extends AbstractTest {

    private static final DockerSpannerConfig dockerSpannerConfig;

    static {
        dockerSpannerConfig = new DockerSpannerConfig();
    }


    @BeforeAll
    public static void setupDockerSpannerInstanceDatabase() throws InterruptedException {
        dockerSpannerConfig.setupDocker();
        dockerSpannerConfig.setupSpanner();
        dockerSpannerConfig.setupDatabase();
        dockerSpannerConfig.fillDatabase();
    }

    @AfterAll
    public static void stopDocker() {
        dockerSpannerConfig.closeSpanner();
        dockerSpannerConfig.stopDocker();
    }

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
        assertEquals("Hello", "Hello");
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
        assertEquals(pwd.getFirstName(), "Denys");
        assertEquals(pwd.getLastName(), "Matsenko");
        assertEquals(pwd.getEmail(), "idanchik47@gmail.com");
        assertEquals(pwd.getAddress(), "Akademika Valtera,14");
        assertEquals(pwd.getPhoneNumber(), "380669410135");
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
        assertEquals(pd.getAddress(), "Akademika Valtera,14");
        assertEquals(pd.getPhoneNumber(), "380669410135");
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
            personDetails.setUserId(personService.getLatestPersonEntry().getId());
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
            int id = personService.getLatestPersonDetailsEntry().getDetailsId();

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

            int status = mvcResult.getResponse().getStatus();
            String content = mvcResult.getResponse().getContentAsString();
            assertEquals(202, status);
            assertEquals(content, "\"ACCEPTED\"");
        }

        @Test
        public void deletePersonAndDetailsIfExist() throws Exception {
            MvcResult mvcResult;

            Person person = personService.getLatestPersonEntry();
            mvcResult = mvc.perform(MockMvcRequestBuilders.delete(PERSONS_URL + person.getId()))
                    .andReturn();

            assertEquals(200, mvcResult.getResponse().getStatus());
            assertEquals("\"OK\"", mvcResult.getResponse().getContentAsString());
        }
    }

    @Nested
    @SpringBootTest
    public class PersonWithInfoTest {

        @MockBean
        PersonRepository personRepository;
        @MockBean
        PeronsDetailsRepository peronsDetailsRepository;
        @MockBean
        SpannerTemplate spannerTemplate;

        @Autowired
        PersonService personService;

        @Test
        public void testGetPersonById() {
            when(personRepository.findById(1)).thenReturn(java.util.Optional
                    .of(new Person(1, "Denys", "Matsenko", "idanchik47@gmail.com")));
            Person person = personService.getPersonById(1);
            assertNotNull(person);
            assertEquals(1, person.getId());
        }

        @Test
        public void testGetPersonDetailsById() {
            when(peronsDetailsRepository.findById(1)).thenReturn(java.util.Optional
                    .of(new PersonDetails(1, 1, "some steet", "49543975348")));
            PersonDetails personDetails = personService.getPersonDetailsById(1);
            assertNotNull(personDetails);
            assertEquals(1, personDetails.getUserId());
        }
    }

    @Nested
    @SpringBootTest
    public class PersonWithPostsTest extends AbstractTest {

        @Autowired
        PostService postService;

        private static final String PWP_URL = "http://localhost:8080/pwp/";
        private static final String POSTS_URL = "http://localhost:8080/posts/";

        @BeforeEach
        public void init() {
            setUp();
        }

        @Test
        public void getPersonWithPostsByUserId() throws Exception {
            int userId = 1;
            MvcResult mvcResult = mvc
                    .perform(MockMvcRequestBuilders.get(PWP_URL + userId)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

            int status = mvcResult.getResponse().getStatus();
            assertEquals(200, status);
            String content = mvcResult.getResponse().getContentAsString();
            PersonWithPosts pwp = super.mapFromJson(content, PersonWithPosts.class);
            Assertions.assertTrue(pwp.getId() == userId && pwp.getPosts().get(0).getId() == userId);
        }

        @Test
        public void getPostsList() throws Exception {
            MvcResult mvcResult = mvc
                    .perform(MockMvcRequestBuilders.get(POSTS_URL)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

            int status = mvcResult.getResponse().getStatus();
            assertEquals(200, status);
            String content = mvcResult.getResponse().getContentAsString();
            Post[] posts = super.mapFromJson(content, Post[].class);
            Assertions.assertTrue(posts.length > 0);
        }

        @Test
        public void getPostsByUserId() throws Exception {
            int userId = 1;
            MvcResult mvcResult = mvc
                    .perform(MockMvcRequestBuilders.get(POSTS_URL + userId).accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

            int status = mvcResult.getResponse().getStatus();
            assertEquals(200, status);
            String content = mvcResult.getResponse().getContentAsString();
            Post[] posts = super.mapFromJson(content, Post[].class);
            assertEquals(userId, posts[0].getUserId());
        }

    }

    @Nested
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
}
