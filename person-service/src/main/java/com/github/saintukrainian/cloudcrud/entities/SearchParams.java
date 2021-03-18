package com.github.saintukrainian.cloudcrud.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Denys Matsenko
 *     <p>The {@code SearchParams} class is used for getting search params as an object
 */
@AllArgsConstructor
@NoArgsConstructor
public class SearchParams {

  @Getter @Setter private String firstName;
}
