package com.github.saintukrainian.cloudcrud.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saintukrainian.cloudcrud.entities.Post;
import com.github.saintukrainian.cloudcrud.exceptions.PersonNotFoundException;
import com.github.saintukrainian.cloudcrud.repositories.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Service
@PropertySource("classpath:url.properties")
@RequiredArgsConstructor
public class PostService {

    @Value("${url.posts}")
    private String POSTS_URL;

    private final RestTemplate restTemplate;
    private final PersonRepository personRepository;

    private final static Logger logger;

    static {
        logger = Logger.getLogger(PostService.class.getName());
        try {
            FileHandler fileHandler = new FileHandler("post.log", true);
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public List<Post> getPostsByUserId(int id) {
        logger.info("Finding posts for user with id=" + id);
        long time = System.currentTimeMillis();
        if (personRepository.existsById(id)) {
            List<Post> posts = List.of(Objects.requireNonNull(restTemplate.getForObject(POSTS_URL + "?userId=" + id, Post[].class)));
            logger.info("Posts found. Time taken: " + (System.currentTimeMillis() - time) + " milliseconds");
            return posts;
        } else {
            logger.warning("Person was not found with id=" + id);
            throw new PersonNotFoundException();
        }
    }

    public List<Post> getAllPosts() {
        logger.info("Finding all posts");
        return List.of(Objects.requireNonNull(restTemplate.getForObject(POSTS_URL, Post[].class)));
    }
}
