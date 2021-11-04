package com.kokabmedia.recipe.services;
import com.kokabmedia.recipe.commands.IngredientCommand;

/*
 * Interface for service layer object.
 * 
 * The practise of coding against an interface implements loose coupling with
 * the @Autowired annotation allowing dependency injection and better unit testing.
 */
public interface IngredientService {

    IngredientCommand findByRecipeIdAndIngredientId(Long recipeId, Long ingredientId);

    IngredientCommand saveIngredientCommand(IngredientCommand command);

    void deleteById(Long recipeId, Long idToDelete);
}