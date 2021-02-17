package com.github.saintukrainian.cloudcrud.restcontrollers;

import java.io.IOException;
import java.util.List;

import com.github.saintukrainian.cloudcrud.entities.Post;

import com.github.saintukrainian.cloudcrud.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;


    @GetMapping("/{id}")
    public List<Post> getByUserId(@PathVariable int id) throws IllegalArgumentException, IOException, InterruptedException {
        List<Post> posts = postService.getPostsByUserId(id);

        if(posts.size() == 0) {
            throw new IllegalArgumentException();
        } else {
            return posts;
        }
    }

    @GetMapping("/")
    public List<Post> getPosts() throws IOException, InterruptedException {
        return postService.getAllPosts();
    }
}
