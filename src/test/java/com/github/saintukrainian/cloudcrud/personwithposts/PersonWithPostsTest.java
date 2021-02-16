package com.github.saintukrainian.cloudcrud.personwithposts;

import com.github.saintukrainian.cloudcrud.entities.PersonWithPosts;
import com.github.saintukrainian.cloudcrud.entities.Post;
import com.github.saintukrainian.cloudcrud.restcontrollers.PostsController;
import com.github.saintukrainian.cloudcrud.service.PostsService;
import com.github.saintukrainian.cloudcrud.spanner.AbstractTest;
import org.apache.catalina.core.ApplicationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PersonWithPostsTest extends AbstractTest {

    @Autowired
    PostsService postsService;

    private static final String PWP_URL = "http://localhost:8080/pwp/";
    private static final String POSTS_URL = "http://localhost:8080/posts/";

    @BeforeEach
    public void init() {
        setUp();
    }

    @Test
    public void getPersonWithPostsByUserId() throws Exception {
        int userId = 1;
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders.get(PWP_URL + userId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        PersonWithPosts pwp = super.mapFromJson(content, PersonWithPosts.class);
        Assertions.assertTrue(pwp.getId() == userId && pwp.getPosts().get(0).getId() == userId);
    }

    @Test
    public void getPostsList() throws Exception {
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders.get(POSTS_URL)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Post[] posts = super.mapFromJson(content, Post[].class);
        Assertions.assertTrue(posts.length > 0);
    }

    @Test
    @Order(value = 10)
    public void getPostsByUserId() throws Exception {
        int userId = 1;
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders.get(POSTS_URL + userId).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Post[] posts = super.mapFromJson(content, Post[].class);
        assertEquals(userId, posts[0].getUserId());
    }
}
