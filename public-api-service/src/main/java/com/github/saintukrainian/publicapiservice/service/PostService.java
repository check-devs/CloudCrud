package com.github.saintukrainian.publicapiservice.service;

import com.github.saintukrainian.publicapiservice.annotations.MeasureExecutionTime;
import com.github.saintukrainian.publicapiservice.entities.Post;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Denys Matsenko
 * @version 1.0.0
 *     <p>The {@code PostService} class is used for sending http requests to a third-party service
 */
@Service
@PropertySource("classpath:url.properties")
public class PostService {

  @Value("${url.posts}")
  private String POSTS_URL;

  private final RestTemplate restTemplate;

  public PostService(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  /**
   * Method for getting posts by person id
   *
   * @param id person id
   * @return list of posts
   */
  @MeasureExecutionTime
  public List<Post> getPostsByUserId(int id) {
    return List.of(
        Objects.requireNonNull(
            restTemplate.getForObject(POSTS_URL + "?userId=" + id, Post[].class)));
  }

  /**
   * Method for getting all posts
   *
   * @return list of posts
   */
  public List<Post> getAllPosts() {
    Optional<List<Post>> posts;
    posts = Optional.of(List.of(restTemplate.getForObject(POSTS_URL, Post[].class)));
    return posts.orElse(Collections.emptyList());
  }
}
