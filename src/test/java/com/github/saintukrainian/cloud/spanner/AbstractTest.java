package com.github.saintukrainian.cloud.spanner;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.github.saintukrainian.cloud.repositories.PeronsDetailsRepository;
import com.github.saintukrainian.cloud.repositories.PersonRepository;
import com.github.saintukrainian.cloud.service.PersonService;
import com.google.cloud.spanner.Instance;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@WebAppConfiguration
public abstract class AbstractTest {
    
   protected MockMvc mvc;
   protected static final String PERSONS_URL = "http://localhost:8080/persons/";
   protected static final String PWD_URL = "http://localhost:8080/pwd/";
   protected static final String PD_URL = "http://localhost:8080/pd/";

   @Autowired
   PersonRepository personRepository;
   @Autowired
   PeronsDetailsRepository peronsDetailsRepository;
   @Autowired
   PersonService personService;
   
   @Autowired
   WebApplicationContext webApplicationContext;

   protected void setUp() {
      mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
   }
   protected String mapToJson(Object obj) throws JsonProcessingException {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writeValueAsString(obj);
   }
   protected <T> T mapFromJson(String json, Class<T> clazz)
      throws JsonParseException, JsonMappingException, IOException {
      
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(json, clazz);
   }
}
