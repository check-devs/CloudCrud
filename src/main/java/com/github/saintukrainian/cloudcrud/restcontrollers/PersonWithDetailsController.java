package com.github.saintukrainian.cloudcrud.restcontrollers;

import com.github.saintukrainian.cloudcrud.entities.PersonWithDetails;
import com.github.saintukrainian.cloudcrud.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Denys Matsenko
 * @version 1.0.0
 * <p>
 * The {@code PersonWithDetailsController} class handles GET requests
 * for getting {@code PersonWithDetails}
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/pwd")
public class PersonWithDetailsController {

    /**
     * {@code PersonService} reference
     */
    private final PersonService personService;

    /**
     * GET method for getting list of Persons with Details
     *
     * @return list of persons with details
     */
    @GetMapping("/")
    public List<PersonWithDetails> getFullPerson() {
        return personService.getAllPersonsWithDetails();
    }

    /**
     * GET method for getting Person with Details by id
     *
     * @param id person id
     * @return person with details
     */
    @GetMapping("/{id}")
    public PersonWithDetails getFullPersonById(@PathVariable int id) {
        return personService.getPersonWithDetailsById(id);
    }
}
