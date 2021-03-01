package com.github.saintukrainian.cloudcrud.spanner;

import com.github.saintukrainian.cloudcrud.entities.*;
import com.github.saintukrainian.cloudcrud.repositories.PersonDetailsRepository;
import com.github.saintukrainian.cloudcrud.repositories.PersonRepository;
import com.github.saintukrainian.cloudcrud.service.PersonService;
import com.github.saintukrainian.cloudcrud.service.PostService;
import com.github.saintukrainian.cloudcrud.spannerconfig.DockerSpannerConfig;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
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

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();
        assertEquals(200, status);
        String content = response.getContentAsString();
        Person[] persons = super.mapFromJson(content, Person[].class);
        Assertions.assertNotNull(persons);

        Person person1 = new Person(1, "Denys", "Matsenko", "idanchik47@gmail.com");
        Person person2 = new Person(2, "Max", "Basov", "scratchy@gmail.com");
        Person person3 = new Person(3, "Kirill", "Ikumapaii", "merlodon@gmail.com");

        assertEquals(person1, persons[0]);
        assertEquals(person2, persons[1]);
        assertEquals(person3, persons[2]);
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
    public void personNotFound() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(PERSONS_URL + 10000)
                                                                .accept(MediaType.APPLICATION_JSON_VALUE))
                                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(404, response.getStatus());
        assertEquals("\"NOT_FOUND\"", response.getContentAsString());
    }

    @Test
    public void personDetailsNotFound() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(PD_URL + 10000)
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

    @Test
    public void personDetailsBadRequest() throws Exception {
        PersonDetails personDetails = new PersonDetails(1,1,"jfjskd", "45849853");
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(PD_URL)
                                                                .content(mapToJson(personDetails))
                                                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                                .accept(MediaType.APPLICATION_JSON_VALUE))
                                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(400, response.getStatus());
        assertEquals("\"BAD_REQUEST\"", response.getContentAsString());
    }

    @Test
    public void getPersonWithDetailsById() throws Exception {
        int userId = 1;

        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders.get(PWD_URL + userId)
                                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();
        String content = response.getContentAsString();

        PersonWithDetails pwd = super.mapFromJson(content, PersonWithDetails.class);
        PersonWithDetails personWithDetails = PersonWithDetails.builder()
                .detailsId(userId)
                .userId(userId)
                .address("Akademika Valtera,14")
                .firstName("Denys")
                .lastName("Matsenko")
                .email("idanchik47@gmail.com")
                .phoneNumber("380669410135")
                .build();

        assertEquals(200, status);
        assertEquals(pwd, personWithDetails);
    }

    @Test
    public void getPersonDetailsById() throws Exception {
        int userId = 1;
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders.get(PD_URL + userId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();
        assertEquals(200, status);
        String content = response.getContentAsString();
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

            MockHttpServletResponse response = mvcResult.getResponse();
            int status = response.getStatus();
            assertEquals(201, status);
            String content = response.getContentAsString();
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

            MockHttpServletResponse response = mvcResult.getResponse();
            int status = response.getStatus();
            assertEquals(201, status);
            String content = response.getContentAsString();
            assertEquals("\"CREATED\"", content);
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

            MockHttpServletResponse response = mvcResult.getResponse();
            int status = response.getStatus();
            assertEquals(202, status);
            String content = response.getContentAsString();
            assertEquals("\"ACCEPTED\"", content);
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
        public void deletePersonAndDetailsIfExist() throws Exception {
            MvcResult mvcResult;

            Person person = personService.getLatestPersonEntry();
            mvcResult = mvc.perform(MockMvcRequestBuilders.delete(PERSONS_URL + person.getId()))
                    .andReturn();

            MockHttpServletResponse response = mvcResult.getResponse();
            assertEquals(200, response.getStatus());
            assertEquals("\"OK\"", response.getContentAsString());
        }
    }

    @Nested
    @SpringBootTest
    public class PersonWithInfoTest {

        @MockBean
        PersonRepository personRepository;
        @MockBean
        PersonDetailsRepository personDetailsRepository;
        @MockBean
        SpannerTemplate spannerTemplate;

        @Autowired
        PersonService personService;

        @Test
        public void testGetPersonById() {
            when(personRepository.findById(1)).thenReturn(java.util.Optional
                    .of(new Person(1, "Denys", "Matsenko", "idanchik47@gmail.com")));
            Person person = personService.getPersonById(1);
            Person personToBeEqualTo = new Person(1, "Denys", "Matsenko", "idanchik47@gmail.com");
            assertNotNull(person);
            assertEquals(person, personToBeEqualTo);
        }

        @Test
        public void testGetPersonDetailsById() {
            when(personDetailsRepository.findById(1)).thenReturn(java.util.Optional
                    .of(new PersonDetails(1, 1, "some steet", "49543975348")));
            PersonDetails personDetails = personService.getPersonDetailsById(1);
            assertNotNull(personDetails);
            assertEquals(1, personDetails.getUserId());
            assertEquals(1, personDetails.getDetailsId());
            assertEquals("49543975348", personDetails.getPhoneNumber());
            assertEquals("some steet", personDetails.getAddress());
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

            MockHttpServletResponse response = mvcResult.getResponse();
            int status = response.getStatus();
            assertEquals(200, status);
            String content = response.getContentAsString();
            PersonWithPosts pwp = super.mapFromJson(content, PersonWithPosts.class);
            Assertions.assertTrue(pwp.getId() == userId && pwp.getPosts().get(0).getId() == userId);
        }

        @Test
        public void getPostsList() throws Exception {
            MvcResult mvcResult = mvc
                    .perform(MockMvcRequestBuilders.get(POSTS_URL)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

            MockHttpServletResponse response = mvcResult.getResponse();
            int status = response.getStatus();
            assertEquals(200, status);
            String content = response.getContentAsString();
            Post[] posts = super.mapFromJson(content, Post[].class);
            Assertions.assertTrue(posts.length > 0);
        }

        @Test
        public void getPostsByUserId() throws Exception {
            int userId = 1;
            MvcResult mvcResult = mvc
                    .perform(MockMvcRequestBuilders.get(POSTS_URL + userId).accept(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

            MockHttpServletResponse response = mvcResult.getResponse();
            int status = response.getStatus();
            assertEquals(200, status);
            String content = response.getContentAsString();
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
            Person person = new Person(1, "Denys", "Matsenko", "idanchik47@gmail.com");
            assertEquals(person, persons[0]);
        }

    }
}
