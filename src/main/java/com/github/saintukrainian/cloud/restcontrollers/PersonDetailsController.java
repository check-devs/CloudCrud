package com.github.saintukrainian.cloud.restcontrollers;

import com.github.saintukrainian.cloud.entities.PersonDetails;
import com.github.saintukrainian.cloud.exceptions.PersonDetailsNotFoundException;
import com.github.saintukrainian.cloud.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pd")
public class PersonDetailsController {

    private final PersonService personService;

    @GetMapping("/{id}")
    public PersonDetails getById(@PathVariable int id) {
        return personService.findPersonDetailsById(id)
                            .orElseThrow(PersonDetailsNotFoundException::new);
    }

    @PostMapping("/")
    @ResponseStatus(code = HttpStatus.CREATED)
    public HttpStatus addDetails(@RequestBody PersonDetails personDetails) {
        personService.savePersonDetails(personDetails);
        return HttpStatus.CREATED;
    }

    @PutMapping("/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public HttpStatus updateDetails(@PathVariable int id,@RequestBody PersonDetails personDetails) {
        personService.updatePersonDetails(id,personDetails);
        return HttpStatus.ACCEPTED;
    }

}
