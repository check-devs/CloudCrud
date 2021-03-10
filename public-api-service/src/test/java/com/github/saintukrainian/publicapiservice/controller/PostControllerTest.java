package com.github.saintukrainian.publicapiservice.controller;

import com.github.saintukrainian.publicapiservice.entities.Post;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PostControllerTest extends AbstractTest {

  private static final String POSTS_URL = "http://localhost:8082/posts/";

  @BeforeEach
  public void init() {
    setUp();
  }

  @Test
  public void getPostsList() throws Exception {
    MvcResult mvcResult =
        mvc.perform(MockMvcRequestBuilders.get(POSTS_URL).accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    MockHttpServletResponse response = mvcResult.getResponse();
    int status = response.getStatus();
    assertEquals(200, status);
    String content = response.getContentAsString();
    Post[] posts = super.mapFromJson(content, Post[].class);
    Assertions.assertTrue(posts.length > 0);
  }

  @Test
  public void getPostsByUserId() throws Exception {
    int userId = 1;
    MvcResult mvcResult =
        mvc.perform(
                MockMvcRequestBuilders.get(POSTS_URL + userId)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    MockHttpServletResponse response = mvcResult.getResponse();
    int status = response.getStatus();
    assertEquals(200, status);
    String content = response.getContentAsString();
    Post[] posts = super.mapFromJson(content, Post[].class);
    assertEquals(userId, posts[0].getUserId());
  }
}
