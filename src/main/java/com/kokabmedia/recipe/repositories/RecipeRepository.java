package com.kokabmedia.recipe.repositories;

import com.kokabmedia.recipe.domain.Recipe;
import org.springframework.data.repository.CrudRepository;

/*
 * Interface that gives access to to CRUD methods for handling data in a database,
 * the CrudRepository interface has methods that perform SQL queries and lets the 
 * application create and update data in the database, it takes an entity class 
 * and the primary key type of that entity as argument.
 */
public interface RecipeRepository extends CrudRepository<Recipe, Long> {
}
