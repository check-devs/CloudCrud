package com.github.saintukrainian.cloudcrud.restcontrollers;

import com.github.saintukrainian.cloudcrud.entities.PersonDetails;
import com.github.saintukrainian.cloudcrud.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @author Denys Matsenko
 * @version 1.0.0
 * <p>
 * The {@code PersonDetailsController} class handles {@code PersonDetails} related requests
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/pd")
public class PersonDetailsController {

    private final PersonService personService;

    @GetMapping("/{id}")
    public PersonDetails getById(@PathVariable int id) {
        return personService.getPersonDetailsById(id);
    }

    @PostMapping("/")
    @ResponseStatus(code = HttpStatus.CREATED)
    public HttpStatus addDetails(@RequestBody PersonDetails personDetails) {
        personService.savePersonDetails(personDetails);
        return HttpStatus.CREATED;
    }

    @PutMapping("/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public HttpStatus updateDetails(@PathVariable int id,
            @RequestBody PersonDetails personDetails) {
        personService.updatePersonDetails(id, personDetails);
        return HttpStatus.ACCEPTED;
    }

}
