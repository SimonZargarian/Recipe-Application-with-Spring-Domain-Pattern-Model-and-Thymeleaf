package com.kokabmedia.recipe.services;

import com.kokabmedia.recipe.commands.IngredientCommand;
import com.kokabmedia.recipe.converters.IngredientCommandToIngredient;
import com.kokabmedia.recipe.converters.IngredientToIngredientCommand;
import com.kokabmedia.recipe.domain.Ingredient;
import com.kokabmedia.recipe.domain.Recipe;
import com.kokabmedia.recipe.repositories.RecipeRepository;
import com.kokabmedia.recipe.repositories.UnitOfMeasureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

//Causes Lombok to generate a logger field.
@Slf4j
/*
* The @Service annotation allows the Spring framework to creates an instance (bean) 
* of this class and manage it with the Spring Application Context (the IOC container)
* that maintains all the beans for the application.  
*
* The@Service annotation lets the Spring framework manage class as a Spring bean. 
* The Spring framework will find the bean with auto-detection when scanning the class 
* path with component scanning. It turns the class into a Spring bean at the auto-scan 
* time.
* 
* @Service annotation allows the IngredientServiceImpl class and to be wired in as dependency 
* to a another object or a bean with the @Autowired annotation.
* 
* The @Service annotation is a specialisation of @Component annotation for more specific 
* use cases.
*/
@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;
    private final RecipeRepository recipeRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;

    public IngredientServiceImpl(IngredientToIngredientCommand ingredientToIngredientCommand,
                                 IngredientCommandToIngredient ingredientCommandToIngredient,
                                 RecipeRepository recipeRepository, UnitOfMeasureRepository unitOfMeasureRepository) {
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
        this.ingredientCommandToIngredient = ingredientCommandToIngredient;
        this.recipeRepository = recipeRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
    }

    @Override
    public IngredientCommand findByRecipeIdAndIngredientId(Long recipeId, Long ingredientId) {

        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);

        if (!recipeOptional.isPresent()){
            //todo impl error handling
            log.error("recipe id not found. Id: " + recipeId);
        }

        Recipe recipe = recipeOptional.get();

        Optional<IngredientCommand> ingredientCommandOptional = recipe.getIngredients().stream()
                .filter(ingredient -> ingredient.getId().equals(ingredientId))
                .map( ingredient -> ingredientToIngredientCommand.convert(ingredient)).findFirst();

        if(!ingredientCommandOptional.isPresent()){
            //todo impl error handling
            log.error("Ingredient id not found: " + ingredientId);
        }

        return ingredientCommandOptional.get();
    }

    @Override
    /*
     * A Transaction involves multiple changes to the database data.
     * 
     * The @Transactional annotation on the class level means that all method in this class will run within the 
     * boundaries of an transaction. If code that apply changes to the database is outside the scope of an
     * transaction it will fail, without the Transaction we will not have a connection to the database.
     * 
     * Transaction handles the relationship with the different database tables (relations).
     * 
     * @Transactional annotation allow us to to make a change in data that effect the database data like updating
     * a Student, the update method and other Entity Manager methods will be contained within a transaction, 
     * the Transaction makes sure that all the actions (steps) that change data in the database are successful 
     * or else the transaction (the process) is rolled back (reversed), if the first step fails then second 
     * step will be reversed.
     * 
     * While a process is within the scope of a transaction the Entity Manager and the Persistence Context
     * keeps track to all the operations and modifications and persist them to the database. The entity  
     * instances and the changes to those instances will be stored in the Persistent Context while the 
     * Transaction is operating. At the start of a Transaction a Persistence Context is created and it is
     * closed at the end of the Transaction.
     * 
     * Only when a transaction (for example within a class or a method) is completed will the database 
     * changes sent out to the database with Hibernate. If there is no transaction then the Persistence Context 
     * will be closed after each Entity Manager method call as each method call will be its own Hibernate session.
     * The Persistence Context will live though the length of a method or as long as the Transaction is running.
     * 
     * Each Transaction in the application is associated with it own Persistence Context that manage the 
     * entities within that specific transaction and every method call with this class will have its own
     * transaction with its own Persistence Context.
     */
    @Transactional
    public IngredientCommand saveIngredientCommand(IngredientCommand command) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(command.getRecipeId());

        if(!recipeOptional.isPresent()){

            log.error("Recipe not found for id: " + command.getRecipeId());
            return new IngredientCommand();
        } else {
            Recipe recipe = recipeOptional.get();

            Optional<Ingredient> ingredientOptional = recipe
                    .getIngredients()
                    .stream()
                    .filter(ingredient -> ingredient.getId().equals(command.getId()))
                    .findFirst();

            if(ingredientOptional.isPresent()){
                Ingredient ingredientFound = ingredientOptional.get();
                ingredientFound.setDescription(command.getDescription());
                ingredientFound.setAmount(command.getAmount());
                ingredientFound.setUom(unitOfMeasureRepository
                        .findById(command.getUom().getId())
                        .orElseThrow(() -> new RuntimeException("UOM NOT FOUND"))); //todo address this
            } else {
                //add new Ingredient
                Ingredient ingredient = ingredientCommandToIngredient.convert(command);
                ingredient.setRecipe(recipe);
                recipe.addIngredient(ingredient);
            }
            
            /* 
             * Entity object get mapped and stored in database by Hibernate and Spring JPA. If the 
    		 * detached object is new it will create a new object (row) if it existing the save()
    		 * method will do a merge operation and update the existing entity. The repository object 
    		 * will return back the saved object,
    		 */
            Recipe savedRecipe = recipeRepository.save(recipe);

            Optional<Ingredient> savedIngredientOptional = savedRecipe.getIngredients().stream()
                    .filter(recipeIngredients -> recipeIngredients.getId().equals(command.getId()))
                    .findFirst();

            //check by description
            if(!savedIngredientOptional.isPresent()){
               
                savedIngredientOptional = savedRecipe.getIngredients().stream()
                        .filter(recipeIngredients -> recipeIngredients.getDescription().equals(command.getDescription()))
                        .filter(recipeIngredients -> recipeIngredients.getAmount().equals(command.getAmount()))
                        .filter(recipeIngredients -> recipeIngredients.getUom().getId().equals(command.getUom().getId()))
                        .findFirst();
            }

            //to do check for fail
            return ingredientToIngredientCommand.convert(savedIngredientOptional.get());
        }

    }

    // Delete an specific ingredient 
    @Override
    public void deleteById(Long recipeId, Long idToDelete) {

        log.debug("Deleting ingredient: " + recipeId + ":" + idToDelete);

        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);

        if(recipeOptional.isPresent()){
            Recipe recipe = recipeOptional.get();
            log.debug("found recipe");

            Optional<Ingredient> ingredientOptional = recipe
                    .getIngredients()
                    .stream()
                    .filter(ingredient -> ingredient.getId().equals(idToDelete))
                    .findFirst();

            if(ingredientOptional.isPresent()){
                log.debug("found Ingredient");
                Ingredient ingredientToDelete = ingredientOptional.get();
                ingredientToDelete.setRecipe(null);
                recipe.getIngredients().remove(ingredientOptional.get());
                recipeRepository.save(recipe);
            }
        } else {
            log.debug("Recipe Id Not found. Id:" + recipeId);
        }
    }
}
