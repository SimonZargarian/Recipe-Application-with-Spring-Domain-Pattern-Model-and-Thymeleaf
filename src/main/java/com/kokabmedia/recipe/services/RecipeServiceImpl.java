package com.kokabmedia.recipe.services;

import com.kokabmedia.recipe.commands.RecipeCommand;
import com.kokabmedia.recipe.converters.RecipeCommandToRecipe;
import com.kokabmedia.recipe.converters.RecipeToRecipeCommand;
import com.kokabmedia.recipe.domain.Recipe;
import com.kokabmedia.recipe.exceptions.NotFoundException;
import com.kokabmedia.recipe.repositories.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

@Slf4j
/*
 * The @Service annotation allows the Spring framework to creates an instance
 * (bean) of this class and manage it with the Spring Application Context (the
 * IOC container) that maintains all the beans for the application.
 *
 * The@Service annotation lets the Spring framework manage class as a Spring
 * bean. The Spring framework will find the bean with auto-detection when
 * scanning the class path with component scanning. It turns the class into a
 * Spring bean at the auto-scan time.
 * 
 * @Service annotation allows the RecipeServiceImpl class and to be wired in as
 * dependency to a another object or a bean with the @Autowired annotation.
 * 
 * The @Service annotation is a specialisation of @Component annotation for more
 * specific use cases.
 */
@Service
public class RecipeServiceImpl implements RecipeService {

	private final RecipeRepository recipeRepository;
	private final RecipeCommandToRecipe recipeCommandToRecipe;
	private final RecipeToRecipeCommand recipeToRecipeCommand;

	public RecipeServiceImpl(RecipeRepository recipeRepository, RecipeCommandToRecipe recipeCommandToRecipe,
			RecipeToRecipeCommand recipeToRecipeCommand) {
		this.recipeRepository = recipeRepository;
		this.recipeCommandToRecipe = recipeCommandToRecipe;
		this.recipeToRecipeCommand = recipeToRecipeCommand;
	}

	@Override
	public Set<Recipe> getRecipes() {
		log.debug("I'm in the service");

		Set<Recipe> recipeSet = new HashSet<>();
		recipeRepository.findAll().iterator().forEachRemaining(recipeSet::add);
		return recipeSet;
	}

	@Override
	public Recipe findById(Long l) {

		Optional<Recipe> recipeOptional = recipeRepository.findById(l);

		if (!recipeOptional.isPresent()) {
			throw new NotFoundException("Recipe Not Found. For ID value: " + l.toString());
		}

		return recipeOptional.get();
	}

	@Override
	/*
	 * A Transaction involves multiple changes to the database data.
	 * 
	 * The @Transactional annotation on the class level means that all method in
	 * this class will run within the boundaries of an transaction. If code that
	 * apply changes to the database is outside the scope of an transaction it will
	 * fail, without the Transaction we will not have a connection to the database.
	 * 
	 * Transaction handles the relationship with the different database tables
	 * (relations).
	 * 
	 * @Transactional annotation allow us to to make a change in data that effect
	 * the database data like updating a Student, the update method and other Entity
	 * Manager methods will be contained within a transaction, the Transaction makes
	 * sure that all the actions (steps) that change data in the database are
	 * successful or else the transaction (the process) is rolled back (reversed),
	 * if the first step fails then second step will be reversed.
	 * 
	 * While a process is within the scope of a transaction the Entity Manager and
	 * the Persistence Context keeps track to all the operations and modifications
	 * and persist them to the database. The entity instances and the changes to
	 * those instances will be stored in the Persistent Context while the
	 * Transaction is operating. At the start of a Transaction a Persistence Context
	 * is created and it is closed at the end of the Transaction.
	 * 
	 * Only when a transaction (for example within a class or a method) is completed
	 * will the database changes sent out to the database with Hibernate. If there is
	 * no transaction then the Persistence Context will be closed after each Entity
	 * Manager method call as each method call will be its own Hibernate session.
	 * The Persistence Context will live though the length of a method or as long as
	 * the Transaction is running.
	 * 
	 * Each Transaction in the application is associated with it own Persistence
	 * Context that manage the entities within that specific transaction and every
	 * method call with this class will have its own transaction with its own
	 * Persistence Context.
	 */
	@Transactional
	public RecipeCommand findCommandById(Long l) {
		return recipeToRecipeCommand.convert(findById(l));
	}

	@Override
	@Transactional
	public RecipeCommand saveRecipeCommand(RecipeCommand command) {
		/*
		 * Command objects gets converted to Entity objects (that is not stored in database yet),
		 * the Command object stores data that has been submitted in the form. 
		 */
		Recipe detachedRecipe = recipeCommandToRecipe.convert(command);

		/* Entity object get mapped and stored in database by Hibernate and Spring JPA. If the 
		 * detached object is new it will create a new object (row) if it existing the save()
		 * method will do a merge operation and update the existing entity. The repository object 
		 * will return back the saved object,
		 */
		Recipe savedRecipe = recipeRepository.save(detachedRecipe);
		log.debug("Saved RecipeId:" + savedRecipe.getId());
		
		// The saved Hibernate Entity object gets converted back to Command object 
		return recipeToRecipeCommand.convert(savedRecipe);
	}

	@Override
	public void deleteById(Long idToDelete) {
		recipeRepository.deleteById(idToDelete);

	}
}