package com.github.saintukrainian.cloudcrud.personwithposts;

import com.github.saintukrainian.cloudcrud.restcontrollers.PostsController;
import com.github.saintukrainian.cloudcrud.service.PostsService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class PersonWithPostsConf {

    @Bean
    public PostsService postsService() {
        return new PostsService();
    }
}
