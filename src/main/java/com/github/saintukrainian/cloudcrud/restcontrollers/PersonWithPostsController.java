package com.github.saintukrainian.cloudcrud.restcontrollers;

import java.io.IOException;

import com.github.saintukrainian.cloudcrud.entities.PersonWithPosts;
import com.github.saintukrainian.cloudcrud.service.PersonService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pwp")
public class PersonWithPostsController {

    private final PersonService personService;

    @GetMapping("/{id}")
    public PersonWithPosts getPersonWithPostsById(@PathVariable int id) throws IOException, InterruptedException {
        return personService.getPersonWithPostsById(id);
    }
    
}
