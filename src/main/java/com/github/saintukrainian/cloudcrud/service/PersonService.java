package com.github.saintukrainian.cloudcrud.service;

import com.github.saintukrainian.cloudcrud.entities.*;
import com.github.saintukrainian.cloudcrud.exceptions.BadRequestException;
import com.github.saintukrainian.cloudcrud.exceptions.PersonDetailsNotFoundException;
import com.github.saintukrainian.cloudcrud.exceptions.PersonNotFoundException;
import com.github.saintukrainian.cloudcrud.repositories.PeronsDetailsRepository;
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
import java.util.concurrent.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Service
@PropertySource("classpath:sql.properties")
@RequiredArgsConstructor
public class PersonService {

    private final PeronsDetailsRepository peronsDetailsRepository;
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


    public Person getPersonById(int id) {
        logger.info("Finding person with id=" + id);
        long time = System.currentTimeMillis();
        Optional<Person> person = personRepository.findById(id);

        if (person.isPresent()) {
            logger.info("Person found. Time taken: " + (System.currentTimeMillis() - time) + " milliseconds");
            return person.get();
        } else {
            logPersonWasNotFoundWithId(id);
            throw new PersonNotFoundException();
        }
    }

    public PersonDetails getPersonDetailsById(int id) {
        Optional<PersonDetails> personDetails = peronsDetailsRepository.findById(id);

        if (personDetails.isPresent()) {
            return personDetails.get();
        } else {
            throw new PersonDetailsNotFoundException();
        }
    }

    public Iterable<Person> findAllPersons() {
        return personRepository.findAll();
    }

    public void savePerson(Person person) throws BadRequestException {
        int newUserId;

        if (person.getId() == 0) {
            newUserId = this.getLatestPersonEntry().getId() + 1;
            person.setId(newUserId);
        } else {
            throw new BadRequestException();
        }
        personRepository.save(person);
    }

    public void updatePerson(int userId, Person person) {
        if (this.checkIfPersonExistsById(userId)) {
            person.setId(userId);
            personRepository.save(person);
        } else {
            logPersonWasNotFoundWithId(userId);
            throw new PersonNotFoundException();
        }
    }

    public void savePersonDetails(PersonDetails personDetails) throws BadRequestException, PersonNotFoundException {
        if (personDetails.getDetailsId() != 0) {
            throw new BadRequestException();
        }

        int userId = personDetails.getUserId();

        if (this.checkIfPersonExistsById(userId)) {
            personDetails.setDetailsId(userId);
            peronsDetailsRepository.save(personDetails);
        } else {
            logPersonWasNotFoundWithId(userId);
            throw new PersonNotFoundException();
        }
    }

    public void updatePersonDetails(int id, PersonDetails personDetails) {
        if (this.checkIfPersonDetailsExistById(id)) {
            personDetails.setUserId(id);
            personDetails.setDetailsId(id);
            peronsDetailsRepository.save(personDetails);
        } else {
            logPersonWasNotFoundWithId(id);
            throw new PersonNotFoundException();
        }
    }

    public void deletePersonById(int id) {
        try {
            personRepository.deleteById(id);
        } catch (IllegalArgumentException e) {
            logPersonFoundWithId(id);
            throw new PersonNotFoundException();
        }
    }

    public void deletePersonDetailsById(int id) {
        try {
            peronsDetailsRepository.deleteById(id);
        } catch (IllegalArgumentException e) {
            throw new PersonDetailsNotFoundException();
        }
    }

    public void suppressedDeletePersonDetailsById(int id) {
        try {
            peronsDetailsRepository.deleteById(id);
        } catch (IllegalArgumentException e) {
        }
    }

    public List<Person> getAllPersonsByFirstName(String firstName) {
        Statement statement = Statement.newBuilder(PERSONS_BY_FIRST_NAME_SQL)
                .bind("firstName")
                .to(firstName)
                .build();
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        return spannerTemplate.query(Person.class, statement, queryOptions);
    }

    public List<PersonWithDetails> getAllPersonsWithDetails() {
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        return spannerTemplate.query(PersonWithDetails.class, Statement.of(PERSONS_WITH_DETAILS_SQL),
                queryOptions);
    }

    public PersonWithDetails getPersonWithDetailsById(int id) throws IllegalArgumentException {
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        Statement statement = Statement.newBuilder(PERSON_WITH_DETAILS_SQL).bind("id")
                .to(id)
                .build();
        List<PersonWithDetails> personWithDetails =
                spannerTemplate.query(PersonWithDetails.class, statement, queryOptions);

        if (CollectionUtils.isNotEmpty(personWithDetails)) {
            logPersonFoundWithId(id);
            return personWithDetails.get(0);
        } else {
            logPersonWasNotFoundWithId(id);
            throw new PersonNotFoundException();
        }
    }


    public PersonWithPosts getPersonWithPostsById(int id) throws InterruptedException, ExecutionException {

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
            logPersonWasNotFoundWithId(id);
            throw new PersonNotFoundException();
        }

    }

    public Person getLatestPersonEntry() throws IndexOutOfBoundsException {
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        List<Person> person = spannerTemplate.query(Person.class, Statement.of(LATEST_PERSON_SQL), queryOptions);

        if (CollectionUtils.isNotEmpty(person)) {
            logPersonFound();
            return person.get(0);
        } else {
            throw new PersonNotFoundException();
        }
    }

    public PersonDetails getLatestPersonDetailsEntry() throws IndexOutOfBoundsException {
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        List<PersonDetails> personDetails =
                spannerTemplate.query(PersonDetails.class, Statement.of(LATEST_PERSON_DETAILS_SQL), queryOptions);

        if (CollectionUtils.isNotEmpty(personDetails)) {
            return personDetails.get(0);
        } else {
            throw new PersonDetailsNotFoundException();
        }
    }

    public boolean checkIfPersonExistsById(int id) {
        return personRepository.existsById(id);
    }

    public boolean checkIfPersonDetailsExistById(int id) {
        return peronsDetailsRepository.existsById(id);
    }

    private void logPersonFoundWithId(int personId) {
        logger.info("Person was found with id=" + personId);
    }

    private void logPersonFound() {
        logger.info("Person was found");
    }

    private void logPersonWasNotFoundWithId(int personId) {
        logger.warning("Person was not found with id=" + personId);
    }
}
