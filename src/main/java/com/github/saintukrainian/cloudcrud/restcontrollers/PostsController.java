package com.github.saintukrainian.cloudcrud.restcontrollers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saintukrainian.cloudcrud.entities.Post;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
public class PostsController {

    private static String SOURCE_URL = "https://jsonplaceholder.typicode.com/posts/";

    @GetMapping("/{id}")
    public List<Post> getByUserId(@PathVariable int id) throws IOException, InterruptedException {
        List<Post> posts;
        ObjectMapper mapper = new ObjectMapper();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().GET().header("accept", "application/json")
                .uri(URI.create(SOURCE_URL + "?userId=" + id)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        posts = mapper.readValue(response.body(), new TypeReference<List<Post>>() {
        });
        return posts;
    }

    @GetMapping("/")
    public List<Post> getPosts() throws IOException, InterruptedException {
        List<Post> posts;
        ObjectMapper mapper = new ObjectMapper();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().GET().header("accept", "application/json")
                .uri(URI.create(SOURCE_URL)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        posts = mapper.readValue(response.body(), new TypeReference<List<Post>>() {});
        return posts;
    }
}
