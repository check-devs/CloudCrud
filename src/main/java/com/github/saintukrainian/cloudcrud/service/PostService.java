package com.github.saintukrainian.cloudcrud.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saintukrainian.cloudcrud.entities.Post;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
@PropertySource("classpath:url.properties")
public class PostService {

    @Value("${url.posts}")
    private String POSTS_URL;
    private final static ObjectMapper mapper = new ObjectMapper();


    public List<Post> getPostsByUserId(int id) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .header("accept", "application/json")
                .uri(URI.create(POSTS_URL + "?userId=" + id))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<List<Post>>() {});
    }

    public List<Post> getAllPosts() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .header("accept", "application/json")
                .uri(URI.create(POSTS_URL))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<List<Post>>() {});
    }
}
