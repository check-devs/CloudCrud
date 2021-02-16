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

import com.github.saintukrainian.cloudcrud.service.PersonService;
import com.github.saintukrainian.cloudcrud.service.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
public class PostsController {


    @Autowired
    private PostsService postsService;


    @GetMapping("/{id}")
    public List<Post> getByUserId(@PathVariable int id) throws IllegalArgumentException, IOException, InterruptedException {
        List<Post> posts = postsService.getPostsByUserId(id);

        if(posts.size() == 0) {
            throw new IllegalArgumentException();
        } else {
            return posts;
        }
    }

    @GetMapping("/")
    public List<Post> getPosts() throws IOException, InterruptedException {
        return postsService.getAllPosts();
    }
}
