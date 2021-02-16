package com.github.saintukrainian.cloudcrud.restcontrollers;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.github.saintukrainian.cloudcrud.entities.Person;
import com.github.saintukrainian.cloudcrud.service.PersonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/persons")
public class PersonsController {

    @Autowired
    private PersonService personService;

    @GetMapping("/")
    public Iterable<Person> getAllPersons() {
        return personService.findAllPersons();
    }

    @GetMapping("/{id}")
    public Person getPersonById(@PathVariable int id) {
        return personService.findPersonById(id)
                            .orElseThrow(IllegalArgumentException::new);
    }

    @PostMapping("/")
    public String addPerson(@RequestBody Person person) {
        if (person.getId() == 0) {
            int latestUserId = personService.findLatestPersonEntry().getId();
            person.setId(latestUserId);
        } else {
            throw new IllegalStateException();
        }
        try {
            personService.savePerson(person);
        } catch (Exception e) {
            return "Bad Request";
        }
        return "Accepted";
    }

    @DeleteMapping("/{id}")
    public String deletePerson(@PathVariable int id) {
        try {
            personService.deletePersonById(id);
            personService.findPersonDetailsById(id)
                         .ifPresent(personDetails -> personService.deletePersonDetailsById(id));
        } catch (Exception e) {
            return "Not Found";
        }
        return "Gone";
    }

    @PutMapping("/")
    public String updatePerson(@RequestBody Person updatedPerson) {
        try {
            personService.savePerson(updatedPerson);
        } catch (Exception e) {
            return "Not Found";
        }
        return "Accepted";
    }

    @GetMapping("/search")
    public List<Person> getPersonsByFirstName(@RequestParam Map<String, String> params) {
        String firstName = params.get("firstName");
        return personService.getAllPersonsByFirstName(firstName);
    }

}
