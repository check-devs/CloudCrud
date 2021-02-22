package com.github.saintukrainian.cloud.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saintukrainian.cloud.entities.Post;
import com.github.saintukrainian.cloud.exceptions.PersonNotFoundException;
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
import java.util.logging.Logger;

@Service
@PropertySource("classpath:url.properties")
@RequiredArgsConstructor
public class PostService {

    @Value("${url.posts}")
    private String POSTS_URL;
    private final static ObjectMapper mapper = new ObjectMapper();

    private final PersonService personService;

    private final static Logger logger = Logger.getLogger(PostService.class.getName());


    public List<Post> getPostsByUserId(int id) throws IOException, InterruptedException {
        logger.info("Finding posts for user with id=" + id);
        if(personService.checkIfPersonExistsById(id)) {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .header("accept", "application/json")
                    .uri(URI.create(POSTS_URL + "?userId=" + id))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
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
