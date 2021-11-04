package com.kokabmedia.recipe.repositories;

import com.kokabmedia.recipe.domain.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/*
 * Interface that gives access to to CRUD methods for handling data in a database,
 * the CrudRepository interface has methods that perform SQL queries and lets the 
 * application create and update data in the database, it takes an entity class 
 * and the primary key type of that entity as argument.
 */
public interface CategoryRepository extends CrudRepository<Category, Long> {

	/* 
	 * JPA custom method with special designed names lets Spring understands that we 
	 * want to retrieve a specific column from the database. 
	 */
    Optional<Category> findByDescription(String description);
}
