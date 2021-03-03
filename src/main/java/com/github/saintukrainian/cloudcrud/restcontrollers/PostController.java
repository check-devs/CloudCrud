package com.github.saintukrainian.cloudcrud.restcontrollers;

import com.github.saintukrainian.cloudcrud.entities.Post;
import com.github.saintukrainian.cloudcrud.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Denys Matsenko
 * @version 1.0.0
 * <p>
 * The {@code PostController} class handles GET requests for {@code Post} entity.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    /**
     * The {@code PostService} reference
     */
    private final PostService postService;

    /**
     * GET method for getting posts by person id
     *
     * @param id person id
     * @return list of person related posts
     */
    @GetMapping("/{id}")
    public List<Post> getPostsByUserId(@PathVariable int id) {
        return postService.getPostsByUserId(id);
    }

    /**
     * GET method for getting posts
     *
     * @return list of posts
     */
    @GetMapping("/")
    public List<Post> getPosts() {
        return postService.getAllPosts();
    }
}
