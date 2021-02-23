package com.github.saintukrainian.cloudcrud.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saintukrainian.cloudcrud.entities.Post;
import com.github.saintukrainian.cloudcrud.exceptions.PersonNotFoundException;
import com.github.saintukrainian.cloudcrud.repositories.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Service
@PropertySource("classpath:url.properties")
@RequiredArgsConstructor
public class PostService {

    @Value("${url.posts}")
    private String POSTS_URL;
    private final static ObjectMapper mapper = new ObjectMapper();

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


    public List<Post> getPostsByUserId(int id) throws IOException, InterruptedException {
        logger.info("Finding posts for user with id=" + id);
        long time = System.currentTimeMillis();
        if(personRepository.existsById(id)) {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .header("accept", "application/json")
                    .uri(URI.create(POSTS_URL + "?userId=" + id))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info("Posts found. Time taken: " + (System.currentTimeMillis() - time) + " milliseconds");
            return mapper.readValue(response.body(), new TypeReference<List<Post>>() {});
        } else  {
            logger.warning("Person was not found with id=" + id);
            throw new PersonNotFoundException();
        }
    }

    public List<Post> getAllPosts() throws IOException, InterruptedException {
        logger.info("Finding all posts");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("accept", "application/json")
                .uri(URI.create(POSTS_URL))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<List<Post>>() {
        });
    }
}
