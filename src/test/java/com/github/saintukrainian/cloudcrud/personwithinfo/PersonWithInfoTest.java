package com.github.saintukrainian.cloudcrud.personwithinfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.github.saintukrainian.cloudcrud.entities.Person;
import com.github.saintukrainian.cloudcrud.entities.PersonDetails;
import com.github.saintukrainian.cloudcrud.repositories.PeronsDetailsRepository;
import com.github.saintukrainian.cloudcrud.repositories.PersonRepository;
import com.github.saintukrainian.cloudcrud.service.PersonService;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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
        Person person = personService.findPersonById(1).get();
        assertEquals(1, person.getId());
    }

    @Test
    public void testGetPersonDetailsById() {
        when(peronsDetailsRepository.findById(1)).thenReturn(java.util.Optional
                .of(new PersonDetails(1, 1,"some steet", "49543975348")));
        PersonDetails personDetails = personService.findPersonDetailsById(1).get();
        assertEquals(1, personDetails.getUserId());
    }
}
