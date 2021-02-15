package com.github.saintukrainian.cloudcrud.restcontrollers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saintukrainian.cloudcrud.entities.PersonWithPosts;
import com.github.saintukrainian.cloudcrud.entities.Post;
import com.github.saintukrainian.cloudcrud.repositories.PersonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pwp")
public class PersonWithPostsContoller {

    private static String POSTS_URL = "https://jsonplaceholder.typicode.com/posts/";

    @Autowired
    private PersonRepository personRepository;

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public HttpStatus notFound(IllegalArgumentException e) {
        return HttpStatus.NOT_FOUND;
    }

    @GetMapping("/{id}")
    public PersonWithPosts getPersonWithPostsById(@PathVariable int id) throws IOException, InterruptedException {
        List<Post> posts;
        PersonWithPosts personWithPosts = new PersonWithPosts();
        ObjectMapper mapper = new ObjectMapper();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().GET().header("accept", "application/json")
                .uri(URI.create(POSTS_URL + "?userId=" + id)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        posts = mapper.readValue(response.body(), new TypeReference<List<Post>>() {});


        personWithPosts.setFieldsWithPersonInfo(personRepository.findById(id).orElseThrow(IllegalArgumentException::new));
        personWithPosts.setPosts(posts);

        return personWithPosts;
    }
    
}
