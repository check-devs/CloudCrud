package com.github.saintukrainian.cloud.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saintukrainian.cloud.entities.*;
import com.github.saintukrainian.cloud.exceptions.BadRequestException;
import com.github.saintukrainian.cloud.exceptions.PersonDetailsNotFoundException;
import com.github.saintukrainian.cloud.exceptions.PersonNotFoundException;
import com.github.saintukrainian.cloud.repositories.PeronsDetailsRepository;
import com.github.saintukrainian.cloud.repositories.PersonRepository;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spring.data.spanner.core.SpannerQueryOptions;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:url.properties")
public class PersonService {

    @Value("${url.posts}")
    private String POSTS_URL;

    private static final ObjectMapper mapper = new ObjectMapper();

    private final PeronsDetailsRepository peronsDetailsRepository;
    private final PersonRepository personRepository;
    private final SpannerTemplate spannerTemplate;

    private final static Logger logger = Logger.getLogger(PersonService.class.getName());


    public Optional<Person> findPersonById(int id) {
        logger.info("Finding person with id=" + id);
        return personRepository.findById(id);
    }

    public Optional<PersonDetails> findPersonDetailsById(int id) {
        logger.info("Finding person details by id=" + id);
        return peronsDetailsRepository.findById(id);
    }

    public Iterable<Person> findAllPersons() {
        logger.info("Finding all persons");
        return personRepository.findAll();
    }

    public void savePerson(Person person) throws BadRequestException {
        logger.info("Saving person with firstName=" + person.getFirstName());
        int newUserId;
        if (person.getId() == 0) {
            newUserId = this.findLatestPersonEntry().getId() + 1;
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
            logger.warning("Can't update person details. Person was not found with id=" + id);
            throw new PersonDetailsNotFoundException();
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
            logPersonFoundWithId(id);
            throw new PersonNotFoundException();
        }
    }


    public PersonWithPosts getPersonWithPostsById(int id) throws IOException, InterruptedException {
        logger.info("Finding all posts for person with id=" + id);
        if(personRepository.existsById(id)) {
            List<Post> posts;
            PersonWithPosts personWithPosts = new PersonWithPosts();
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .header("accept", "application/json")
                    .uri(URI.create(POSTS_URL + "?userId=" + id))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            posts = mapper.readValue(response.body(), new TypeReference<List<Post>>() {
            });
            personWithPosts.setFieldsWithPersonInfo(personRepository.findById(id)
                    .orElseThrow(PersonNotFoundException::new));
            personWithPosts.setPosts(posts);
            return personWithPosts;
        } else {
            logPersonWasNotFoundWithId(id);
            throw new PersonNotFoundException();
        }

    }

    public Person findLatestPersonEntry() throws IndexOutOfBoundsException {
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

    public PersonDetails findLatestPersonDetailsEntry() throws IndexOutOfBoundsException {
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

}
