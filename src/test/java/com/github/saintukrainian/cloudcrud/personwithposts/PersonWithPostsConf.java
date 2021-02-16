package com.github.saintukrainian.cloudcrud.personwithposts;

import com.github.saintukrainian.cloudcrud.service.PostService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class PersonWithPostsConf {

    @Bean
    public PostService postsService() {
        return new PostService();
    }
}
