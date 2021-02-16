package com.github.saintukrainian.cloudcrud.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saintukrainian.cloudcrud.entities.Person;
import com.github.saintukrainian.cloudcrud.entities.PersonDetails;
import com.github.saintukrainian.cloudcrud.entities.PersonWithDetails;
import com.github.saintukrainian.cloudcrud.entities.PersonWithPosts;
import com.github.saintukrainian.cloudcrud.entities.Post;
import com.github.saintukrainian.cloudcrud.repositories.PeronsDetailsRepository;
import com.github.saintukrainian.cloudcrud.repositories.PersonRepository;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spring.data.spanner.core.SpannerQueryOptions;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:url.properties")
public class PersonService {

    @Value("${url.posts}")
    private String POSTS_URL;

    private static final ObjectMapper mapper = new ObjectMapper();

    private PeronsDetailsRepository peronsDetailsRepository;
    private PersonRepository personRepository;
    private SpannerTemplate spannerTemplate;

    @Autowired
    public PersonService(PersonRepository personRepository, PeronsDetailsRepository peronsDetailsRepository,
            SpannerTemplate spannerTemplate) {
        this.personRepository = personRepository;
        this.peronsDetailsRepository = peronsDetailsRepository;
        this.spannerTemplate = spannerTemplate;
    }

    public Optional<Person> findPersonById(int id) {
        return personRepository.findById(id);
    }

    public Optional<PersonDetails> findPersonDetailsById(int id) {
        return peronsDetailsRepository.findById(id);
    }

    public Iterable<Person> findAllPersons() {
        return personRepository.findAll();
    }

    public void savePerson(Person person) {
        personRepository.save(person);
    }

    public void savePersonDetails(PersonDetails personDetails) {
        peronsDetailsRepository.save(personDetails);
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
        String statement = "SELECT * FROM persons WHERE first_name=\"" + firstName + "\"";
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        return spannerTemplate.query(Person.class,
                Statement.of(statement), queryOptions);
    }

    public List<PersonWithDetails> getAllPersonsWithDetails() {
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        String statement = "select p.id, p.first_name, p.last_name, p.email, pd.details_id, pd.address, pd.phone_number from persons p left join person_details pd on p.id=pd.user_id;";
        return spannerTemplate.query(PersonWithDetails.class, Statement.of(statement),
                queryOptions);
    }

    public PersonWithDetails getPersonWithDetailsById(int id) throws IllegalArgumentException {
        if(personRepository.existsById(id)) {
            SpannerQueryOptions queryOptions = new SpannerQueryOptions();
            String statement = "select p.id, p.first_name, p.last_name, p.email, pd.details_id, pd.address, pd.phone_number from persons p left join person_details pd on p.id=pd.user_id where p.id="+ id + " limit 1;";
            return spannerTemplate.query(PersonWithDetails.class, Statement.of(statement), queryOptions)
                    .get(0);
        } else {
            throw new IllegalArgumentException();
        }

    }

    public PersonWithPosts getPersonWithPostsById(int id) throws IOException, InterruptedException {
        List<Post> posts;
        PersonWithPosts personWithPosts = new PersonWithPosts();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                                    .newBuilder()
                                    .GET()
                                    .header("accept", "application/json")
                                    .uri(URI.create(POSTS_URL + "?userId=" + id))
                                    .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        posts = mapper.readValue(response.body(), new TypeReference<List<Post>>() {});


        personWithPosts.setFieldsWithPersonInfo(personRepository.findById(id)
                                                                .orElseThrow(IllegalArgumentException::new));
        personWithPosts.setPosts(posts);
        return personWithPosts;
    }

    public Person findLatestPersonEntry() throws IndexOutOfBoundsException{
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        String statement = "select * from persons order by id desc limit 1;";
        return spannerTemplate.query(Person.class, Statement.of(statement), queryOptions).get(0);
    }

    public PersonDetails findLatestPersonDetailsEntry() throws IndexOutOfBoundsException{
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        String statement = "select * from person_details order by details_id desc limit 1;";
        return spannerTemplate.query(PersonDetails.class, Statement.of(statement), queryOptions).get(0);
    }

    public boolean checkIfPersonExistsById(int id) {
        return personRepository.existsById(id);
    }

    public boolean checkIfPersonDetailsExistById(int id) {
        return peronsDetailsRepository.existsById(id);
    }

}
