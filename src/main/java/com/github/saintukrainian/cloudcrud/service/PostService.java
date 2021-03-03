package com.github.saintukrainian.cloudcrud.service;

import com.github.saintukrainian.cloudcrud.annotations.MeasureExecutionTime;
import com.github.saintukrainian.cloudcrud.entities.Post;
import com.github.saintukrainian.cloudcrud.exceptions.PersonNotFoundException;
import com.github.saintukrainian.cloudcrud.repositories.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

/**
 * @author Denys Matsenko
 * @version 1.0.0
 * <p>
 * The {@code PostService} class is used for sending http requests to a third-party service
 */
@Service
@PropertySource("classpath:url.properties")
public class PostService {

    @Value("${url.posts}")
    private String POSTS_URL;

    private final RestTemplate restTemplate;
    private final PersonRepository personRepository;

    public PostService(RestTemplateBuilder restTemplateBuilder, PersonRepository personRepository) {
        this.restTemplate = restTemplateBuilder.build();
        this.personRepository = personRepository;
    }


    /**
     * Method for getting posts by person id
     *
     * @param id person id
     * @return list of posts
     */
    @MeasureExecutionTime
    public List<Post> getPostsByUserId(int id) {
        if (personRepository.existsById(id)) {
            return List.of(Objects.requireNonNull(restTemplate.getForObject(POSTS_URL + "?userId=" + id, Post[].class)));
        } else {
            throw new PersonNotFoundException();
        }
    }

    /**
     * Method for getting all posts
     *
     * @return list of posts
     */
    public List<Post> getAllPosts() {
        return List.of(Objects.requireNonNull(restTemplate.getForObject(POSTS_URL, Post[].class)));
    }
}
