package com.github.saintukrainian.cloudcrud.restcontrollers;

import java.util.List;
import java.util.Optional;

import com.github.saintukrainian.cloudcrud.entities.Person;
import com.github.saintukrainian.cloudcrud.entities.PersonDetails;
import com.github.saintukrainian.cloudcrud.service.PersonService;
import com.google.common.collect.Iterables;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pd")
public class PersonDetailsController {

    @Autowired
    private PersonService personService;

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public HttpStatus notFound(IllegalArgumentException e) {
        return HttpStatus.NOT_FOUND;
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public HttpStatus BadRequest(IllegalStateException e) {
        return HttpStatus.BAD_REQUEST;
    }

    @GetMapping("/{id}")
    public PersonDetails getById(@PathVariable int id) {
        return personService.findPersonDetailsById(id).orElseThrow(IllegalArgumentException::new);
    }

    @PostMapping("/")
    public String addDetails(@RequestBody PersonDetails personDetails) {
        if(personDetails.getDetailsId() != 0) {
            throw new IllegalStateException();
        }
        
        Optional<Person> person = personService.findPersonById(personDetails.getUserId());

        if (person.isPresent()) {
            personDetails.setDetailsId(personDetails.getUserId());
            personService.savePersonDetails(personDetails);
            return "Details added";
        } else {
            throw new IllegalArgumentException();
        }

    }

    @PutMapping("/")
    public String updateDetails(@RequestBody PersonDetails personDetails) {
        List<PersonDetails> details = List
                .of(Iterables.toArray(personService.findAllPersonDetails(), PersonDetails.class));
        Optional<PersonDetails> detail = details.stream().filter(d -> d.getDetailsId() == personDetails.getDetailsId())
                .findFirst();
        if (detail.isPresent()) {
            personService.savePersonDetails(personDetails);
            return "Details updated";
        } else {
            throw new IllegalArgumentException();
        }
    }

}
