package com.github.saintukrainian.cloudcrud.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Denys Matsenko
 *     <p>The {@code Post} class describes Posts of {@code Person} class entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {

  private int userId;
  private int id;
  private String title;
  private String body;
}
