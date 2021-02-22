package com.github.saintukrainian.cloud.spanner;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.saintukrainian.cloud.entities.*;
import com.github.saintukrainian.cloud.repositories.PeronsDetailsRepository;
import com.github.saintukrainian.cloud.repositories.PersonRepository;
import com.github.saintukrainian.cloud.service.PersonService;
import com.github.saintukrainian.cloud.service.PostService;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.NoCredentials;
import com.google.cloud.spanner.*;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import com.google.spanner.admin.database.v1.CreateDatabaseMetadata;
import com.google.spanner.admin.instance.v1.CreateInstanceMetadata;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@PropertySource("classpath:docker-java.properties")
public class SpannerTests extends AbstractTest {

    private static final DockerClientConfig dockerClientConfig;
    private static final DockerHttpClient dockerHttpClient;
    private static final DockerClient dockerClient;
    private static final Logger logger;
    private static String containerId;
    private static Spanner spanner;

    static {
        dockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        dockerHttpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(dockerClientConfig.getDockerHost())
                .sslConfig(dockerClientConfig.getSSLConfig())
                .build();
        dockerClient = DockerClientImpl.getInstance(dockerClientConfig, dockerHttpClient);
        logger = Logger.getLogger(SpannerTests.class.getName());
    }


    @BeforeAll
    public static void setupDockerSpannerInstanceDatabase() {

        // setting up docker
        System.setProperty("SPANNER_EMULATOR_HOST", "http://localhost:9010/");

        logger.info("Starting container >>>>>>>>");
        CreateContainerResponse containerResponse = dockerClient.createContainerCmd("gcr.io/cloud-spanner-emulator/emulator:latest")
                .withPortBindings(PortBinding.parse("9010:9010"), PortBinding.parse("9020:9020"))
                .exec();
        containerId = containerResponse.getId();
        dockerClient.startContainerCmd(containerId).exec();
        logger.info("Container with id=" + containerId + " is being executed >>>>>>>>");
        String projectId = "test-project";
        spanner = SpannerOptions.newBuilder()
                .setProjectId(projectId)
                .setEmulatorHost(System.getProperty("SPANNER_EMULATOR_HOST"))
                .setCredentials(NoCredentials.getInstance())
                .build()
                .getService();
        logger.info(System.getProperty("SPANNER_EMULATOR_HOST"));
        InstanceAdminClient instanceAdminClient = spanner.getInstanceAdminClient();

        // Set Instance configuration.
        String configId = "emulator-config";
        int nodeCount = 1;
        String instanceId = "test-instance";
        String databaseName = "cloudcrud-testdb";

        // Create an InstanceInfo object that will be used to create the instance.
        InstanceInfo instanceInfo =
                InstanceInfo.newBuilder(InstanceId.of(projectId, instanceId))
                        .setInstanceConfigId(InstanceConfigId.of(projectId, configId))
                        .setNodeCount(nodeCount)
                        .setDisplayName(instanceId)
                        .build();
        OperationFuture<Instance, CreateInstanceMetadata> operation =
                instanceAdminClient.createInstance(instanceInfo);
        try {
            // Wait for the createInstance operation to finish.
            Instance instance = operation.get();
            logger.info("Instance " + instance.getId() +" was successfully created");
        } catch (ExecutionException e) {
            logger.warning(
                    "Error: Creating instance " + instanceInfo.getId() +" failed with error message " + e.getMessage());
        } catch (InterruptedException e) {
            logger.warning("Error: Waiting for createInstance operation to finish was interrupted");
        }

        DatabaseId dbId = DatabaseId.of(projectId, instanceId, databaseName);
        DatabaseAdminClient dbAdminClient = spanner.getDatabaseAdminClient();


        // creating database
        OperationFuture<Database, CreateDatabaseMetadata> op =
                dbAdminClient.createDatabase(
                        dbId.getInstanceId().getInstance(),
                        dbId.getDatabase(),
                        Arrays.asList(
                                "CREATE TABLE persons (" +
                                        "id INT64, first_name STRING(MAX), " +
                                        "last_name STRING(MAX), " +
                                        "email STRING(MAX)" +
                                        ") PRIMARY KEY (id)",
                                "CREATE TABLE person_details (" +
                                        "details_id INT64, " +
                                        "user_id INT64, " +
                                        "address STRING(MAX), p" +
                                        "hone_number STRING(MAX)" +
                                        ") PRIMARY KEY (details_id)"));

        try {
            // Initiate the request which returns an OperationFuture.
            Database db = op.get();
            logger.info("Created database [" + db.getId() + "]");
        } catch (ExecutionException e) {
            // If the operation failed during execution, expose the cause.
            throw (SpannerException) e.getCause();
        } catch (InterruptedException e) {
            // Throw when a thread is waiting, sleeping, or otherwise occupied,
            // and the thread is interrupted, either before or during the activity.
            throw SpannerExceptionFactory.propagateInterrupt(e);
        }

        // filling database
        DatabaseClient dbClient = spanner.getDatabaseClient(dbId);
        dbClient
                .readWriteTransaction()
                .run(
                        (TransactionRunner.TransactionCallable<Void>) transaction -> {
                            String sql =
                                    "INSERT INTO persons (id, first_name, last_name, email) VALUES "
                                            + "(1, 'Denys', 'Matsenko', 'idanchik47@gmail.com'), "
                                            + "(2, 'Max', 'Basov', 'scratchy@gmail.com'), "
                                            + "(3, 'Kirill', 'Ikumapaii', 'merlodon@gmail.com')";
                            long rowCount = transaction.executeUpdate(Statement.of(sql));
                            logger.info(rowCount + " records inserted.\n");
                            return null;
                        });

        dbClient
                .readWriteTransaction()
                .run(
                        (TransactionRunner.TransactionCallable<Void>) transaction -> {
                            String sql =
                                    "INSERT INTO person_details(details_id, user_id, address, phone_number) VALUES "
                                            + "(1, 1, 'Akademika Valtera,14', '380669410135'), "
                                            + "(2, 2, 'Saltovka, 16', '35849856895'), "
                                            + "(3, 3, 'Moskalevka, 17', '454567856784')";
                            long rowCount = transaction.executeUpdate(Statement.of(sql));
                            logger.info(rowCount + " records inserted.\n");
                            return null;
                        });
    }

    @AfterAll
    public static void stopDocker() {
        logger.info("Closing Spanner Instance...");
        spanner.close();
        logger.info("Spanner Instance has been closed!");

        logger.info("Shutting down container >>>>>>>>");
        dockerClient.stopContainerCmd(containerId).exec();
        logger.info("Container with id=" + containerId + " has been shut down >>>>>>>>");
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
            Person person = personService.findPersonById(1).orElse(null);
            assertNotNull(person);
            assertEquals(1, person.getId());
        }

        @Test
        public void testGetPersonDetailsById() {
            when(peronsDetailsRepository.findById(1)).thenReturn(java.util.Optional
                    .of(new PersonDetails(1, 1,"some steet", "49543975348")));
            PersonDetails personDetails = personService.findPersonDetailsById(1).orElse(null);
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
