package com.github.saintukrainian.cloudcrud.restcontrollers;

import com.github.saintukrainian.cloudcrud.entities.PersonWithPosts;
import com.github.saintukrainian.cloudcrud.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * @author Denys Matsenko
 * @version 1.0.0
 * <p>
 * The {@code PersonWithPostsController} class handles GET request for getting person with posts by id
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/pwp")
public class PersonWithPostsController {

    /**
     * {@code PersonService} reference
     */
    private final PersonService personService;

    /**
     * GET method for
     * @param id person id
     * @return person with posts
     * @throws InterruptedException is thrown by {@code CompletableFuture} class
     * @throws ExecutionException is thrown by {@code CompletableFuture} class
     */
    @GetMapping("/{id}")
    public PersonWithPosts getPersonWithPostsById(@PathVariable int id) throws ExecutionException, InterruptedException {
        return personService.getPersonWithPostsById(id);
    }

}
