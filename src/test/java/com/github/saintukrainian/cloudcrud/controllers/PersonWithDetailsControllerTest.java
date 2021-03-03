package com.github.saintukrainian.cloudcrud.controllers;

import com.github.saintukrainian.cloudcrud.entities.PersonWithDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PersonWithDetailsControllerTest extends AbstractTest {

    @BeforeEach
    public void init() {
        setUp();
    }

    @Test
    public void getPersonWithDetailsById() throws Exception {
        int userId = 1;

        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders.get(PWD_URL + userId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();
        String content = response.getContentAsString();

        PersonWithDetails pwd = super.mapFromJson(content, PersonWithDetails.class);
        PersonWithDetails personWithDetails = PersonWithDetails.builder()
                .detailsId(userId)
                .userId(userId)
                .address("Akademika Valtera,14")
                .firstName("Denys")
                .lastName("Matsenko")
                .email("idanchik47@gmail.com")
                .phoneNumber("380669410135")
                .build();

        assertEquals(200, status);
        assertEquals(pwd, personWithDetails);
    }

    @Test
    public void personWithDetailsNotFound() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(PWD_URL + 10000)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(404, response.getStatus());
        assertEquals("\"NOT_FOUND\"", response.getContentAsString());
    }
}
