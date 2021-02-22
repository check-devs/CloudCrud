package com.github.saintukrainian.cloud.restcontrollers;

import com.github.saintukrainian.cloud.entities.PersonWithPosts;
import com.github.saintukrainian.cloud.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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
