package com.github.saintukrainian.cloudcrud.restcontrollers;

import com.github.saintukrainian.cloudcrud.entities.PersonDetails;
import com.github.saintukrainian.cloudcrud.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String addDetails(@RequestBody PersonDetails personDetails) {
        if(personDetails.getDetailsId() != 0) {
            throw new IllegalStateException();
        }

        if (personService.checkIfPersonExistsById(personDetails.getUserId())) {
            personDetails.setDetailsId(personDetails.getUserId());
            personService.savePersonDetails(personDetails);
            return "Details added";
        } else {
            throw new IllegalArgumentException();
        }

    }

    @PutMapping("/")
    public String updateDetails(@RequestBody PersonDetails personDetails) {
        if (personService.checkIfPersonDetailsExistById(personDetails.getDetailsId())) {
            personService.savePersonDetails(personDetails);
            return "Details updated";
        } else {
            throw new IllegalArgumentException();
        }
    }

}
