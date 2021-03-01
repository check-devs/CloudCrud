package com.github.saintukrainian.cloudcrud.restcontrollers;

import com.github.saintukrainian.cloudcrud.entities.Person;
import com.github.saintukrainian.cloudcrud.entities.SearchParams;
import com.github.saintukrainian.cloudcrud.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Denys Matsenko
 * @version 1.0.0
 * <p>
 * The {@code PersonController} class handles all requests related to {@code Person} entity.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/persons")
public class PersonController {

    /**
     * {@code PersonService} class reference
     */
    private final PersonService personService;

    /**
     * GET method for getting all persons
     *
     * @return list of persons
     */
    @GetMapping("/")
    public Iterable<Person> getAllPersons() {
        return personService.findAllPersons();
    }

    /**
     * GET method for getting person by id
     *
     * @param id person id
     * @return person
     */
    @GetMapping("/{id}")
    public Person getPersonById(@PathVariable int id) {
        return personService.getPersonById(id);
    }

    /**
     * POST method for saving person
     *
     * @return HttpStatus
     */
    @PostMapping("/")
    @ResponseStatus(code = HttpStatus.CREATED)
    public HttpStatus addPerson(@RequestBody Person person) {
        personService.savePerson(person);
        return HttpStatus.CREATED;
    }

    /**
     * DELETE method for deleting person by id
     *
     * @param id person id
     * @return HttpStatus
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public HttpStatus deletePerson(@PathVariable int id) {
        personService.deletePersonById(id);
        personService.suppressedDeletePersonDetailsById(id);
        return HttpStatus.OK;
    }

    /**
     * PUT method for updating person by id
     *
     * @param userId        person id
     * @param updatedPerson new person data
     * @return HttpStatus
     */
    @PutMapping("/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public HttpStatus updatePerson(@PathVariable("id") int userId, @RequestBody Person updatedPerson) {
        personService.updatePerson(userId, updatedPerson);
        return HttpStatus.ACCEPTED;
    }

    /**
     * POST method for getting persons by first name
     *
     * @param searchParams first name
     * @return list of persons
     */
    @PostMapping("/search")
    public List<Person> getPersonsByFirstName(@RequestBody SearchParams searchParams) {
        return personService.getAllPersonsByFirstName(searchParams.getFirstName());
    }

}
