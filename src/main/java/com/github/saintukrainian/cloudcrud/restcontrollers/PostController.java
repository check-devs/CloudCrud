package com.github.saintukrainian.cloudcrud.restcontrollers;

import com.github.saintukrainian.cloudcrud.entities.Post;
import com.github.saintukrainian.cloudcrud.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @GetMapping("/{id}")
    public List<Post> getPostsByUserId(@PathVariable int id) throws IllegalArgumentException, IOException, InterruptedException {
        return postService.getPostsByUserId(id);
    }

    @GetMapping("/")
    public List<Post> getPosts() throws IOException, InterruptedException {
        return postService.getAllPosts();
    }
}
