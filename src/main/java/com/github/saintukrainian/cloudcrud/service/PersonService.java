package com.github.saintukrainian.cloudcrud.service;

import java.util.List;
import java.util.Optional;

import com.github.saintukrainian.cloudcrud.entities.Person;
import com.github.saintukrainian.cloudcrud.entities.PersonDetails;
import com.github.saintukrainian.cloudcrud.entities.PersonWithDetails;
import com.github.saintukrainian.cloudcrud.repositories.PeronsDetailsRepository;
import com.github.saintukrainian.cloudcrud.repositories.PersonRepository;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spring.data.spanner.core.SpannerQueryOptions;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

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
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        List<Person> persons = spannerTemplate.query(Person.class,
                Statement.of("SELECT * FROM persons WHERE first_name=\"" + firstName + "\""), queryOptions);
        return persons;
    }

    public List<PersonWithDetails> getAllPersonsWithDetails() {
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        String statement = 
        "select p.id, p.first_name, p.last_name, p.email, pd.details_id, pd.address, pd.phone_number from persons p left join person_details pd on p.id=pd.user_id;";
        List<PersonWithDetails> persons = spannerTemplate.query(PersonWithDetails.class, Statement.of(statement), queryOptions);
        return persons;
    }

    public PersonWithDetails getPersonWithDetailsById(int id) {
        SpannerQueryOptions queryOptions = new SpannerQueryOptions();
        String statement = 
        "select p.id, p.first_name, p.last_name, p.email, pd.details_id, pd.address, pd.phone_number from persons p left join person_details pd on p.id=pd.user_id where p.id=" + id + ";";
        PersonWithDetails person = spannerTemplate.query(PersonWithDetails.class, Statement.of(statement), queryOptions).get(0);
        return person;
    }

}
