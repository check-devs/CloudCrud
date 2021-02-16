package com.github.saintukrainian.cloudcrud.restcontrollers;

import java.util.List;

import com.github.saintukrainian.cloudcrud.entities.PersonWithDetails;
import com.github.saintukrainian.cloudcrud.service.PersonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pwd")
public class PersonWithDetailsController {

    @Autowired
    private PersonService personService;

    @GetMapping("/")
    public List<PersonWithDetails> getFullPerson() {
        return personService.getAllPersonsWithDetails();
    }

    @GetMapping("/{id}")
    public PersonWithDetails getFullPersonById(@PathVariable int id) {
        return personService.getPersonWithDetailsById(id);
    }
}
