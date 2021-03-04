package com.github.saintukrainian.cloudcrud.service;

import com.github.saintukrainian.cloudcrud.annotations.MeasureExecutionTime;
import com.github.saintukrainian.cloudcrud.entities.Person;
import com.github.saintukrainian.cloudcrud.entities.Post;
import com.github.saintukrainian.cloudcrud.exceptions.PersonNotFoundException;
import com.github.saintukrainian.cloudcrud.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author Denys Matsenko
 * @version 1.0.0
 *     <p>The {@code AsyncServiceCalls} class is used to send the specific PostService or
 *     PersonService methods asynchronously
 */
@Component
@PropertySource("classpath:url.properties")
public class AsyncCallsService {

  private final PersonRepository personRepository;
  private final RestTemplate restTemplate;

  public AsyncCallsService(
      RestTemplateBuilder restTemplateBuilder, PersonRepository personRepository) {
    this.restTemplate = restTemplateBuilder.build();
    this.personRepository = personRepository;
  }

  @Value("${url.posts}")
  private String POSTS_URL;

  /**
   * Method for getting person by id from repository asynchronously
   *
   * @param id user id
   * @return {@code CompletableFuture} instance with {@code Person} class param.
   */
  @Async
  @MeasureExecutionTime
  public CompletableFuture<Person> getPersonById(int id) {
    Optional<Person> person = personRepository.findById(id);

    return CompletableFuture.completedFuture(person.orElseThrow(PersonNotFoundException::new));
  }

  /**
   * Method for getting posts by person id from remote service asynchronously
   *
   * @param id user id
   * @return {@code CompletableFuture} instance with the list of posts.
   */
  @Async
  @MeasureExecutionTime
  public CompletableFuture<List<Post>> getPostsByUserId(int id) {
    if (personRepository.existsById(id)) {
      return CompletableFuture.completedFuture(
          List.of(
              Objects.requireNonNull(
                  restTemplate.getForObject(POSTS_URL + "?userId=" + id, Post[].class))));
    } else {
      throw new PersonNotFoundException();
    }
  }
}
