package com.github.saintukrainian.cloudcrud.controllers;

import com.github.saintukrainian.cloudcrud.entities.PersonWithPosts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersonWithPostsControllerTest extends AbstractTest {

    private static final String PWP_URL = "http://localhost:8080/pwp/";

    @BeforeEach
    public void init() {
        setUp();
    }

    @Test
    public void getPersonWithPostsByUserId() throws Exception {
        int userId = 1;
        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders.get(PWP_URL + userId)
                        .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();
        assertEquals(200, status);
        String content = response.getContentAsString();
        PersonWithPosts pwp = super.mapFromJson(content, PersonWithPosts.class);
        Assertions.assertTrue(pwp.getId() == userId
                && pwp.getPosts().get(0).getId() == userId);
    }
}
