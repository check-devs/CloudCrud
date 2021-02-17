package com.github.saintukrainian.cloudcrud.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

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


    public Optional<Person> findPersonById(int id) {
        return personRepository.findById(id);
    }

    public Optional<PersonDetails> findPersonDetailsById(int id) {
        return peronsDetailsRepository.findById(id);
    }

    public Iterable<Person> findAllPersons() {
        return personRepository.findAll();
    }

    public void savePerson(Person person) throws BadRequestException {
        if (person.getId() == 0) {
            int latestUserId = this.findLatestPersonEntry().getId();
            person.setId(latestUserId + 1);
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
            throw new PersonNotFoundException();
        }
    }

    public void savePersonDetails(PersonDetails personDetails) throws BadRequestException, PersonNotFoundException {
        if (personDetails.getDetailsId() != 0) {
            throw new BadRequestException();
        }

        if (this.checkIfPersonExistsById(personDetails.getUserId())) {
            personDetails.setDetailsId(personDetails.getUserId());
            peronsDetailsRepository.save(personDetails);
        } else {
            throw new PersonNotFoundException();
        }
    }

    public void updatePersonDetails(int id, PersonDetails personDetails) {
        if (this.checkIfPersonDetailsExistById(id)) {
            personDetails.setUserId(id);
            personDetails.setDetailsId(id);
            peronsDetailsRepository.save(personDetails);
        } else {
            throw new PersonNotFoundException();
        }
    }

    public void deletePersonById(int id) {
        personRepository.deleteById(id);
    }

    public Iterable<PersonDetails> findAllPersonDetails() {
        return peronsDetailsRepository.findAll();
    }

    public void deletePersonDetailsById(int id) {
        peronsDetailsRepository.deleteById(id);
    }

    public List<Person> getAllPersonsByFirstName(String firstName) {
        Statement statement = Statement.newBuilder("SELECT * FROM persons WHERE first_name=@firstName;")
                .bind("firstName")
                .to(firstName)
                .build();
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        return spannerTemplate.query(Person.class, statement, queryOptions);
    }

    public List<PersonWithDetails> getAllPersonsWithDetails() {
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        String statement = "select p.id, p.first_name, p.last_name, p.email, pd.details_id, pd.address, pd.phone_number from persons p left join person_details pd on p.id=pd.user_id;";
        return spannerTemplate.query(PersonWithDetails.class, Statement.of(statement),
                queryOptions);
    }

    public PersonWithDetails getPersonWithDetailsById(int id) throws IllegalArgumentException {
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        Statement statement = Statement.newBuilder("select p.id, p.first_name, p.last_name, p.email, pd.details_id, pd.address, pd.phone_number from persons p left join person_details pd on p.id=pd.user_id where p.id=@id;")
                .bind("id")
                .to(id)
                .build();
        List<PersonWithDetails> personWithDetails = spannerTemplate.query(PersonWithDetails.class, statement, queryOptions);
        if(CollectionUtils.isNotEmpty(personWithDetails)) {
            return personWithDetails.get(0);
        } else {
            throw new PersonNotFoundException();
        }
    }


    public PersonWithPosts getPersonWithPostsById(int id) throws IOException, InterruptedException {
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
    }

    public Person findLatestPersonEntry() throws IndexOutOfBoundsException {
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        String statement = "select * from persons order by id desc limit 1;";
        List<Person> person = spannerTemplate.query(Person.class, Statement.of(statement), queryOptions);
        if (CollectionUtils.isNotEmpty(person)) {
            return person.get(0);
        } else {
            throw new PersonNotFoundException();
        }
    }

    public PersonDetails findLatestPersonDetailsEntry() throws IndexOutOfBoundsException {
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        String statement = "select * from person_details order by details_id desc limit 1;";
        List<PersonDetails> personDetails = spannerTemplate.query(PersonDetails.class, Statement.of(statement), queryOptions);
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

}
