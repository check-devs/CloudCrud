package com.github.saintukrainian.cloudcrud.service;

import com.github.saintukrainian.cloudcrud.entities.*;
import com.github.saintukrainian.cloudcrud.exceptions.BadRequestException;
import com.github.saintukrainian.cloudcrud.exceptions.PersonDetailsNotFoundException;
import com.github.saintukrainian.cloudcrud.exceptions.PersonNotFoundException;
import com.github.saintukrainian.cloudcrud.repositories.PersonDetailsRepository;
import com.github.saintukrainian.cloudcrud.repositories.PersonRepository;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spring.data.spanner.core.SpannerQueryOptions;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author Denys Matsenko
 * @version 1.0.0
 * <p>
 * The {@code PersonService} class is a service for {@code PersonRepository} and {@code PersonDetailsRepository}.<br>
 * It contains sql statements, spanner template instance and a reference to {@code PostService} class.<br>
 * It is used for manipulating person related data.
 */
@Service
@PropertySource("classpath:sql.properties")
@RequiredArgsConstructor
public class PersonService {

    private final PersonDetailsRepository personDetailsRepository;
    private final PersonRepository personRepository;
    private final SpannerTemplate spannerTemplate;
    private final PostService postService;

    @Value("${sql.persons-with-details}")
    private String PERSONS_WITH_DETAILS_SQL;

    @Value("${sql.person-with-details}")
    private String PERSON_WITH_DETAILS_SQL;

    @Value("${sql.persons-by-first-name}")
    private String PERSONS_BY_FIRST_NAME_SQL;

    @Value("${sql.latest-person-details}")
    private String LATEST_PERSON_DETAILS_SQL;

    @Value("${sql.latest-person}")
    private String LATEST_PERSON_SQL;

    private final static Logger logger;

    static {
        logger = Logger.getLogger(PersonService.class.getName());
        try {
            FileHandler fileHandler = new FileHandler("person.log", true);
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Method for getting person by id from repository
     *
     * @param id user id
     * @return {@code Person} instance
     */
    public Person getPersonById(int id) {
        long time = System.currentTimeMillis();
        Optional<Person> person = personRepository.findById(id);

        if (person.isPresent()) {
            logger.info("Person found. Time taken: " + (System.currentTimeMillis() - time) + " milliseconds");
            return person.get();
        } else {
            throw new PersonNotFoundException();
        }
    }

    /**
     * Method for getting person details by id from repository
     *
     * @param id details id
     * @return {@code PersonDetails} instance
     */
    public PersonDetails getPersonDetailsById(int id) {
        Optional<PersonDetails> personDetails = personDetailsRepository.findById(id);

        if (personDetails.isPresent()) {
            return personDetails.get();
        } else {
            throw new PersonDetailsNotFoundException();
        }
    }

    /**
     * Method for getting all persons
     *
     * @return persons
     */
    public Iterable<Person> findAllPersons() {
        return personRepository.findAll();
    }

    /**
     * Method for saving person
     *
     * @param person new person
     */
    public void savePerson(Person person) {
        int newUserId;

        if (person.getId() == 0) {
            newUserId = this.getLatestPersonEntry().getId() + 1;
            person.setId(newUserId);
        } else {
            throw new BadRequestException();
        }
        personRepository.save(person);
    }

    /**
     * Method for updating person
     *
     * @param userId person id
     * @param person new person data
     */
    public void updatePerson(int userId, Person person) {
        if (this.checkIfPersonExistsById(userId)) {
            person.setId(userId);
            personRepository.save(person);
        } else {
            throw new PersonNotFoundException();
        }
    }

    /**
     * Method for saving person details
     *
     * @param personDetails new person details
     */
    public void savePersonDetails(PersonDetails personDetails) {
        if (personDetails.getDetailsId() != 0) {
            throw new BadRequestException();
        }

        int userId = personDetails.getUserId();

        if (this.checkIfPersonExistsById(userId)) {
            personDetails.setDetailsId(userId);
            personDetailsRepository.save(personDetails);
        } else {
            throw new PersonNotFoundException();
        }
    }

    /**
     * Method for updating person details
     *
     * @param id            details id
     * @param personDetails new person details data
     */
    public void updatePersonDetails(int id, PersonDetails personDetails) {
        if (this.checkIfPersonDetailsExistById(id)) {
            personDetails.setUserId(id);
            personDetails.setDetailsId(id);
            personDetailsRepository.save(personDetails);
        } else {
            throw new PersonNotFoundException();
        }
    }

    /**
     * Method for deleting person by id
     *
     * @param id id of person to be deleted
     */
    public void deletePersonById(int id) {
        if (checkIfPersonExistsById(id)) {
            personRepository.deleteById(id);
        } else {
            throw new PersonNotFoundException();
        }
    }

    /**
     * Method for deleting person details
     *
     * @param id details id
     */
    public void deletePersonDetailsById(int id) {
        if (checkIfPersonDetailsExistById(id)) {
            personDetailsRepository.deleteById(id);
        } else {
            throw new PersonDetailsNotFoundException();
        }
    }

    /**
     * Suppressed delete of person details (it doesn't throw any exceptions)
     *
     * @param id details id
     */
    public void suppressedDeletePersonDetailsById(int id) {
        try {
            personDetailsRepository.deleteById(id);
        } catch (IllegalArgumentException ignored) {
        }
    }

    /**
     * Method for getting all person by first name
     *
     * @param firstName first name
     * @return list of persons with the same first name
     */
    public List<Person> getAllPersonsByFirstName(String firstName) {
        Statement statement = Statement.newBuilder(PERSONS_BY_FIRST_NAME_SQL)
                .bind("firstName")
                .to(firstName)
                .build();
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        return spannerTemplate.query(Person.class, statement, queryOptions);
    }

    /**
     * Method for getting all persons with their details
     *
     * @return list of persons with details
     */
    public List<PersonWithDetails> getAllPersonsWithDetails() {
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        return spannerTemplate.query(PersonWithDetails.class, Statement.of(PERSONS_WITH_DETAILS_SQL),
                queryOptions);
    }

    /**
     * Method for getting a person with details by id
     *
     * @param id person id
     * @return person with details
     */
    public PersonWithDetails getPersonWithDetailsById(int id) {
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        Statement statement = Statement.newBuilder(PERSON_WITH_DETAILS_SQL).bind("id")
                .to(id)
                .build();
        List<PersonWithDetails> personWithDetails =
                spannerTemplate.query(PersonWithDetails.class, statement, queryOptions);

        if (CollectionUtils.isNotEmpty(personWithDetails)) {
            return personWithDetails.get(0);
        } else {
            throw new PersonNotFoundException();
        }
    }


    /**
     * Method for getting person with posts by person's id
     *
     * @param id person's id
     * @return person with posts
     * @throws InterruptedException exception related to {@code CompletableFuture} class
     * @throws ExecutionException   exception related to {@code CompletableFuture} class
     */
    public PersonWithPosts getPersonWithPostsById(int id) throws ExecutionException, InterruptedException {

        long time = System.currentTimeMillis();

        if (personRepository.existsById(id)) {
            CompletableFuture<Person> person = CompletableFuture.supplyAsync(() -> getPersonById(id));
            CompletableFuture<List<Post>> posts = CompletableFuture.supplyAsync(() -> postService.getPostsByUserId(id));
            PersonWithPosts personWithPosts = new PersonWithPosts();

            personWithPosts.setFieldsWithPersonInfo(person.get());
            personWithPosts.setPosts(posts.get());

            logger.info("Person with posts method completed in " +
                    (System.currentTimeMillis() - time) + " milliseconds");
            return personWithPosts;
        } else {
            throw new PersonNotFoundException();
        }

    }

    /**
     * Method for getting the latest person in the database
     *
     * @return latest person
     */
    public Person getLatestPersonEntry() {
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        List<Person> person = spannerTemplate.query(Person.class, Statement.of(LATEST_PERSON_SQL), queryOptions);

        if (CollectionUtils.isNotEmpty(person)) {
            return person.get(0);
        } else {
            throw new PersonNotFoundException();
        }
    }

    /**
     * Method for getting the latest person details in the database
     *
     * @return latest person details
     */
    public PersonDetails getLatestPersonDetailsEntry() {
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        List<PersonDetails> personDetails =
                spannerTemplate.query(PersonDetails.class, Statement.of(LATEST_PERSON_DETAILS_SQL), queryOptions);

        if (CollectionUtils.isNotEmpty(personDetails)) {
            return personDetails.get(0);
        } else {
            throw new PersonDetailsNotFoundException();
        }
    }

    /**
     * Method for checking whether the person exists or not
     *
     * @param id person's id
     * @return true if exists
     */
    public boolean checkIfPersonExistsById(int id) {
        return personRepository.existsById(id);
    }

    /**
     * Method for checking whether the person details exist or not
     *
     * @param id details id
     * @return true if they exist
     */
    public boolean checkIfPersonDetailsExistById(int id) {
        return personDetailsRepository.existsById(id);
    }
}
