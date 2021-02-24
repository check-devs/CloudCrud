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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PeronsDetailsRepository peronsDetailsRepository;
    private final PersonRepository personRepository;
    private final SpannerTemplate spannerTemplate;
    private final PostService postService;

    private ExecutorService executorService = Executors.newFixedThreadPool(2);

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
        if(person.isPresent()) {
            logger.info("Person found. Time taken: " + (System.currentTimeMillis() - time) + " milliseconds");
            return person.get();
        } else {
            logPersonWasNotFoundWithId(id);
            throw new PersonNotFoundException();
        }
    }

    public PersonDetails getPersonDetailsById(int id) {
        logger.info("Finding person details by id=" + id);
        Optional<PersonDetails> personDetails = peronsDetailsRepository.findById(id);
        if(personDetails.isPresent()) {
            return personDetails.get();
        } else {
            logger.warning("Person details were not found with id=" + id);
            throw new PersonDetailsNotFoundException();
        }
    }

    public Iterable<Person> findAllPersons() {
        logger.info("Finding all persons");
        return personRepository.findAll();
    }

    public void savePerson(Person person) throws BadRequestException {
        logger.info("Saving person with firstName=" + person.getFirstName());
        int newUserId;
        if (person.getId() == 0) {
            newUserId = this.getLatestPersonEntry().getId() + 1;
            person.setId(newUserId);
        } else {
            logger.warning("Bad request for saving person. ID was present.");
            throw new BadRequestException();
        }
        personRepository.save(person);
        logger.info("Person saved with new id=" + newUserId);
    }

    public void updatePerson(int userId, Person person) {
        logger.info("Updating person with id=" + userId);
        if (this.checkIfPersonExistsById(userId)) {
            person.setId(userId);
            personRepository.save(person);
            logger.info("Person with id=" + userId + " was successfully updated!");
        } else {
            logPersonWasNotFoundWithId(userId);
            throw new PersonNotFoundException();
        }
    }

    public void savePersonDetails(PersonDetails personDetails) throws BadRequestException, PersonNotFoundException {
        if (personDetails.getDetailsId() != 0) {
            logger.warning("Bad request for saving person details. Details ID was present!");
            throw new BadRequestException();
        }

        int userId = personDetails.getUserId();
        logger.info("Adding person details for person with id=" + userId);
        if (this.checkIfPersonExistsById(userId)) {
            personDetails.setDetailsId(userId);
            peronsDetailsRepository.save(personDetails);
            logger.info("Person details were successfully set for person with id=" + userId);
        } else {
            logPersonWasNotFoundWithId(userId);
            throw new PersonNotFoundException();
        }
    }

    public void updatePersonDetails(int id, PersonDetails personDetails) {
        logger.info("Updating person details for person with id=" + personDetails.getUserId());
        if (this.checkIfPersonDetailsExistById(id)) {
            personDetails.setUserId(id);
            personDetails.setDetailsId(id);
            peronsDetailsRepository.save(personDetails);
            logger.info("Person details were successfully updated for person with id=" + id);
        } else {
            logPersonWasNotFoundWithId(id);
            throw new PersonNotFoundException();
        }
    }

    public void deletePersonById(int id) {
        logger.info("Deleting person with id=" + id);
        try {
            personRepository.deleteById(id);
            logger.info("Person was deleted with id=" + id);
        } catch (IllegalArgumentException e) {
            logPersonFoundWithId(id);
            throw new PersonNotFoundException();
        }
    }

    public void deletePersonDetailsById(int id) {
        logger.info("Deleting person details for person with id=" + id);
        try {
            peronsDetailsRepository.deleteById(id);
            logger.info("Person details were successfully deleted for person with id=" + id);
        } catch (IllegalArgumentException e) {
            logger.warning("Can't delete person details. Person was not found with id=" + id);
            throw new PersonDetailsNotFoundException();
        }
    }

    public void suppressedDeletePersonDetailsById(int id) {
        logger.info("Deleting person details for person with id=" + id);
        try {
            peronsDetailsRepository.deleteById(id);
            logger.info("Person details were successfully deleted for person with id=" + id);
        } catch (IllegalArgumentException e) {
            logger.warning("Can't delete person details. Person was not found with id=" + id);
        }
    }

    public List<Person> getAllPersonsByFirstName(String firstName) {
        logger.info("Finding all person by firstName=" + firstName);
        Statement statement = Statement.newBuilder("SELECT * FROM persons WHERE first_name=@firstName;")
                .bind("firstName")
                .to(firstName)
                .build();
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        return spannerTemplate.query(Person.class, statement, queryOptions);
    }

    public List<PersonWithDetails> getAllPersonsWithDetails() {
        logger.info("Finding all persons with details");
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        String statement = "select p.id, p.first_name, p.last_name, p.email, pd.details_id, pd.address, pd.phone_number from persons p left join person_details pd on p.id=pd.user_id;";
        return spannerTemplate.query(PersonWithDetails.class, Statement.of(statement),
                queryOptions);
    }

    public PersonWithDetails getPersonWithDetailsById(int id) throws IllegalArgumentException {
        logger.info("Finding person with details by id=" + id);
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        Statement statement = Statement.newBuilder("select p.id, p.first_name, p.last_name, p.email, pd.details_id, pd.address, pd.phone_number from persons p left join person_details pd on p.id=pd.user_id where p.id=@id;")
                .bind("id")
                .to(id)
                .build();
        List<PersonWithDetails> personWithDetails = spannerTemplate.query(PersonWithDetails.class, statement, queryOptions);
        if (CollectionUtils.isNotEmpty(personWithDetails)) {
            logPersonFoundWithId(id);
            return personWithDetails.get(0);
        } else {
            logPersonWasNotFoundWithId(id);
            throw new PersonNotFoundException();
        }
    }


    public PersonWithPosts getPersonWithPostsById(int id) throws InterruptedException, TimeoutException, ExecutionException {
        logger.info("Finding all posts for person with id=" + id);
        long time = System.currentTimeMillis();
        if(personRepository.existsById(id)) {
            Future<Person> person = getPersonByIdAsync(id);
            Future<List<Post>> posts = getPostsByPersonIdAsync(id);

            PersonWithPosts personWithPosts = new PersonWithPosts();
            personWithPosts.setFieldsWithPersonInfo(person.get(1000, TimeUnit.MILLISECONDS));
            personWithPosts.setPosts(posts.get(1000, TimeUnit.MILLISECONDS));
            logger.info("Person with posts method completed in " + (System.currentTimeMillis() - time) + " milliseconds");
            return personWithPosts;
        } else {
            logPersonWasNotFoundWithId(id);
            throw new PersonNotFoundException();
        }

    }

    public Person getLatestPersonEntry() throws IndexOutOfBoundsException {
        logger.info("Finding latest person entry");
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        String statement = "select * from persons order by id desc limit 1;";
        List<Person> person = spannerTemplate.query(Person.class, Statement.of(statement), queryOptions);
        if (CollectionUtils.isNotEmpty(person)) {
            logPersonFound();
            return person.get(0);
        } else {
            logger.warning("Hmm, table seems to be empty.");
            throw new PersonNotFoundException();
        }
    }

    public PersonDetails getLatestPersonDetailsEntry() throws IndexOutOfBoundsException {
        logger.info("Finding latest person details entry");
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        String statement = "select * from person_details order by details_id desc limit 1;";
        List<PersonDetails> personDetails = spannerTemplate.query(PersonDetails.class, Statement.of(statement), queryOptions);
        if (CollectionUtils.isNotEmpty(personDetails)) {
            return personDetails.get(0);
        } else {
            logger.warning("Hmm, table seems to be empty.");
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

    private void logPersonWasNotFound() {
        logger.warning("Person was not found");
    }

    public Future<Person> getPersonByIdAsync(int id) {
        System.out.println("In findPersonByIdAsync");
        return executorService.submit(() -> getPersonById(id));
    }

    public Future<List<Post>> getPostsByPersonIdAsync(int id) {
        System.out.println("In  findPostsByPersonIdAsync");
        return executorService.submit(() -> postService.getPostsByUserId(id));
    }

}
