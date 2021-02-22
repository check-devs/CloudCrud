package com.github.saintukrainian.cloud.restcontrollers;

import com.github.saintukrainian.cloud.entities.PersonWithDetails;
import com.github.saintukrainian.cloud.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pwd")
public class PersonWithDetailsController {

    private final PersonService personService;

    @GetMapping("/")
    public List<PersonWithDetails> getFullPerson() {
        return personService.getAllPersonsWithDetails();
    }

    @GetMapping("/{id}")
    public PersonWithDetails getFullPersonById(@PathVariable int id) {
        return personService.getPersonWithDetailsById(id);
    }
}
