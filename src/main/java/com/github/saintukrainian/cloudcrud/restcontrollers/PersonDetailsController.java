package com.github.saintukrainian.cloudcrud.restcontrollers;

import com.github.saintukrainian.cloudcrud.entities.PersonDetails;
import com.github.saintukrainian.cloudcrud.service.PersonService;
import org.checkerframework.checker.lock.qual.Holding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pd")
public class PersonDetailsController {

    @Autowired
    private PersonService personService;

    @GetMapping("/{id}")
    public PersonDetails getById(@PathVariable int id) {
        return personService.findPersonDetailsById(id)
                            .orElseThrow(IllegalArgumentException::new);
    }

    @PostMapping("/")
    @ResponseStatus(code = HttpStatus.CREATED)
    public HttpStatus addDetails(@RequestBody PersonDetails personDetails) {
        if(personDetails.getDetailsId() != 0) {
            throw new IllegalStateException();
        }

        if (personService.checkIfPersonExistsById(personDetails.getUserId())) {
            personDetails.setDetailsId(personDetails.getUserId());
            personService.savePersonDetails(personDetails);
            return HttpStatus.CREATED;
        } else {
            throw new IllegalArgumentException();
        }

    }

    @PutMapping("/")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public HttpStatus updateDetails(@RequestBody PersonDetails personDetails) {
        if (personService.checkIfPersonDetailsExistById(personDetails.getDetailsId())) {
            personService.savePersonDetails(personDetails);
            return HttpStatus.ACCEPTED;
        } else {
            throw new IllegalArgumentException();
        }
    }

}
