package com.github.saintukrainian.cloud.restcontrollers;

import com.github.saintukrainian.cloud.entities.Person;
import com.github.saintukrainian.cloud.entities.SearchParams;
import com.github.saintukrainian.cloud.exceptions.PersonNotFoundException;
import com.github.saintukrainian.cloud.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/persons")
public class PersonController {

    private final PersonService personService;

    @GetMapping("/")
    public Iterable<Person> getAllPersons() {
        return personService.findAllPersons();
    }

    @GetMapping("/{id}")
    public Person getPersonById(@PathVariable int id) {
        return personService.findPersonById(id);
    }

    @PostMapping("/")
    @ResponseStatus(code = HttpStatus.CREATED)
    public HttpStatus addPerson(@RequestBody Person person) {
        personService.savePerson(person);
        return HttpStatus.CREATED;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public HttpStatus deletePerson(@PathVariable int id) {
        personService.deletePersonById(id);
        personService.suppressedDeletePersonDetailsById(id);
        return HttpStatus.OK;
    }

    @PutMapping("/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public HttpStatus updatePerson(@PathVariable("id") int userId, @RequestBody Person updatedPerson) {
        personService.updatePerson(userId, updatedPerson);
        return HttpStatus.ACCEPTED;
    }

    @PostMapping("/search")
    public List<Person> getPersonsByFirstName(@RequestBody SearchParams searchParams) {
        return personService.getAllPersonsByFirstName(searchParams.getFirstName());
    }

}
