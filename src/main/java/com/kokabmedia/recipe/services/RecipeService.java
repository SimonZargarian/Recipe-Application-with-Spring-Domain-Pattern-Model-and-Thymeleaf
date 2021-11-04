package com.kokabmedia.recipe.services;

import com.kokabmedia.recipe.commands.RecipeCommand;
import com.kokabmedia.recipe.domain.Recipe;

import java.util.Set;

/*
 * Interface for service layer object.
 * 
 * The practise of coding against an interface implements loose coupling with
 * the @Autowired annotation allowing dependency injection and better unit testing.
 */
public interface RecipeService {

    Set<Recipe> getRecipes();

    Recipe findById(Long l);

    RecipeCommand findCommandById(Long l);

    RecipeCommand saveRecipeCommand(RecipeCommand command);

    void deleteById(Long idToDelete);
}


