package com.github.saintukrainian.cloudcrud.controllers;

import com.github.saintukrainian.cloudcrud.entities.PersonDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PersonDetailsControllerTest extends AbstractTest {

  @BeforeEach
  public void init() {
    setUp();
  }

  @Test
  public void getPersonDetailsById() throws Exception {
    int userId = 1;
    MvcResult mvcResult =
        mvc.perform(
            MockMvcRequestBuilders.get(PD_URL + userId)
                .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

    MockHttpServletResponse response = mvcResult.getResponse();
    int status = response.getStatus();
    assertEquals(200, status);
    String content = response.getContentAsString();
    PersonDetails pd = super.mapFromJson(content, PersonDetails.class);
    assertEquals(pd.getUserId(), userId);
    assertEquals(pd.getDetailsId(), userId);
    assertEquals(pd.getAddress(), "Akademika Valtera,14");
    assertEquals(pd.getPhoneNumber(), "380669410135");
  }

  @Test
  public void addPersonDetails() throws Exception {
    PersonDetails personDetails = new PersonDetails();
    personDetails.setUserId(personService.getLatestPersonEntry().getId());
    personDetails.setAddress("some address");
    personDetails.setPhoneNumber("45894365846");

    String inputJson = super.mapToJson(personDetails);
    MvcResult mvcResult =
        mvc.perform(
            MockMvcRequestBuilders.post(PD_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson))
            .andReturn();

    MockHttpServletResponse response = mvcResult.getResponse();
    int status = response.getStatus();
    assertEquals(201, status);
    String content = response.getContentAsString();
    assertEquals("\"CREATED\"", content);
  }

  @Test
  public void updatePersonDetails() throws Exception {
    PersonDetails personDetails = new PersonDetails();
    personDetails.setAddress("new address");
    personDetails.setPhoneNumber("45894365846");
    int id = personService.getLatestPersonDetailsEntry().getDetailsId();

    String inputJson = super.mapToJson(personDetails);
    MvcResult mvcResult =
        mvc.perform(
            MockMvcRequestBuilders.put(PD_URL + id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson))
            .andReturn();

    MockHttpServletResponse response = mvcResult.getResponse();
    int status = response.getStatus();
    assertEquals(202, status);
    String content = response.getContentAsString();
    assertEquals("\"ACCEPTED\"", content);
  }

  @Test
  public void personDetailsNotFound() throws Exception {
    MvcResult mvcResult =
        mvc.perform(
            MockMvcRequestBuilders.get(PD_URL + 10000).accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();
    MockHttpServletResponse response = mvcResult.getResponse();
    assertEquals(404, response.getStatus());
    assertEquals("\"NOT_FOUND\"", response.getContentAsString());
  }

  @Test
  public void personDetailsBadRequest() throws Exception {
    PersonDetails personDetails = new PersonDetails(1, 1, "jfjskd", "45849853");
    MvcResult mvcResult =
        mvc.perform(
            MockMvcRequestBuilders.post(PD_URL)
                .content(mapToJson(personDetails))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();
    MockHttpServletResponse response = mvcResult.getResponse();
    assertEquals(400, response.getStatus());
    assertEquals("\"BAD_REQUEST\"", response.getContentAsString());
  }
}
