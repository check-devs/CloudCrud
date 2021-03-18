package com.github.saintukrainian.cloudcrud.service;

import com.github.saintukrainian.cloudcrud.entities.Post;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "post-service")
public interface PostServiceClient {

  @GetMapping("/posts/{userId}")
  List<Post> getPostsByUserId(@PathVariable int userId);
}
