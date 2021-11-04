package com.kokabmedia.recipe.controllers;

import com.kokabmedia.recipe.commands.IngredientCommand;
import com.kokabmedia.recipe.commands.RecipeCommand;
import com.kokabmedia.recipe.commands.UnitOfMeasureCommand;
import com.kokabmedia.recipe.services.IngredientService;
import com.kokabmedia.recipe.services.RecipeService;
import com.kokabmedia.recipe.services.UnitOfMeasureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
//Enables Lombok to generate a logger field.
@Slf4j
/* 
 * This class will function as a controller and as servlet that responds to HTTP requests.
 * 
 * The dispatcher servlet is the Front Controller for the Spring MVC framework 
 * handles all the requests of the root (/) of the web application. 
 * 
 * Dispatcher servlet knows all the HTTP request methods GET, POST, PUT AND DELETE 
 * and what java methods they are mapped to with annotations. Dispatcher servlet will 
 * delegate which controller should handle a specific request. Dispatcher servlet looks 
 * at the URL and the request method
 * 
* The @Controller annotation allows the Spring framework to creates an instance (bean) 
* of this class and manage it with the Spring Application Context (the IOC container)
* that maintains all the beans for the application.  
*
* The @Controller annotation lets the Spring framework manage class as a Spring bean. 
* The Spring framework will find the bean with auto-detection when scanning the class 
* path with component scanning. It turns the class into a Spring bean at the auto-scan 
* time.
* 
* @Controller annotation allows the IngredientController class and to be wired in as dependency 
* to a another object or a bean with the @Autowired annotation.
* 
* The @Controller annotation is a specialisation of @Component annotation for more specific 
* use cases.
*/
@Controller
public class IngredientController {
	
	private final IngredientService ingredientService;
    private final RecipeService recipeService;
    private final UnitOfMeasureService unitOfMeasureService;

    public IngredientController(IngredientService ingredientService, RecipeService recipeService, UnitOfMeasureService unitOfMeasureService) {
        this.ingredientService = ingredientService;
        this.recipeService = recipeService;
        this.unitOfMeasureService = unitOfMeasureService;
    }

    /*
	 * This method returns an user with a specific id.
	 * 
	 * When a GET HTTP request is sent to a certain URL and that URL contains a path which
	 * is declared on the @GetMapping annotation, in this case the appended 
	 * "/recipe/{recipeId}/ingredients", this method will be called.
	 * 
	 * The @GetMapping annotation will bind and make listIngredients() method respond to a 
	 * HTTP GET request.
	 * 
	 * The ("/recipe/{recipeId}/ingredients") parameter allows the method to read the appended 
	 * integer after the URL http://localhost:8080/recipe/ as a path variable that is attached, 
	 * so when a String is appended after http://localhost:8080/recipe/ with a GET HTTP request the 
	 * retrieveUser(PathVariable String recipeId) method is called. 
	 * 
	 * The name of the "/{recipeId}" parameter must match the @PathVariable annotation argument 
	 * String recipeId.
	 */
    @GetMapping("/recipe/{recipeId}/ingredients")
    /*
     * The @PathVariable annotation will make the path variable in the URL available
	 * for this listIngredients() method via the method argument. When a user String recipeId
	 * is appended to http://localhost:8080/recipe/ it can be handled by the listIngredients()
	 * method.
	 */	
    public String listIngredients(@PathVariable String recipeId, Model model){
        log.debug("Getting ingredient list for recipe id: " + recipeId);

        // use command object to avoid lazy load errors in Thymeleaf.
        model.addAttribute("recipe", recipeService.findCommandById(Long.valueOf(recipeId)));

        return "recipe/ingredient/list";
    }
    
    
    @GetMapping("recipe/{recipeId}/ingredient/{id}/show")
    public String showRecipeIngredient(@PathVariable String recipeId,
                                       @PathVariable String id, Model model){
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(Long.valueOf(recipeId), Long.valueOf(id)));
        return "recipe/ingredient/show";
    }

    @GetMapping("recipe/{recipeId}/ingredient/new")
    public String newRecipe(@PathVariable String recipeId, Model model){

        //make sure we have a good id value
        RecipeCommand recipeCommand = recipeService.findCommandById(Long.valueOf(recipeId));

        //need to return back parent id for hidden form property
        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setRecipeId(Long.valueOf(recipeId));
        model.addAttribute("ingredient", ingredientCommand);

        //init uom
        ingredientCommand.setUom(new UnitOfMeasureCommand());

        model.addAttribute("uomList",  unitOfMeasureService.listAllUoms());

        return "recipe/ingredient/ingredientform";
    }

    @GetMapping("recipe/{recipeId}/ingredient/{id}/update")
    public String updateRecipeIngredient(@PathVariable String recipeId,
                                         @PathVariable String id, Model model){
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(Long.valueOf(recipeId), Long.valueOf(id)));

        model.addAttribute("uomList", unitOfMeasureService.listAllUoms());
        return "recipe/ingredient/ingredientform";
    }
   
    /*
   	 * This method creates a new recipe or updates a recipe with HTTP POST request 
   	 * containing a HTML request body.
   	 * 
   	 * The saveOrUpdate() method will be a web service endpoint that converts HTML
   	 * paylod into a java object.
   	 * 
   	 * When HTTP POST request is sent to a certain URL and that URL contains a path which
   	 * is declared on the @PostMapping annotation then this method will be called, 
   	 * in this case the appended "recipe".
   	 * 
   	 * The @PostMapping annotation will bind and make saveOrUpdate() method respond to
   	 * a HTTP POST request. 
   	 */
    @PostMapping("recipe/{recipeId}/ingredient") 
    /*
     * The @Valid annotation enables validation on User class
     *      
	 * The IngredientCommand object will be mapped to the form (view) with @ModelAttribute 
	 * parameter annotation and make the data available to and from the form.
	 * 
	 * The @ModelAttribute parameter annotation will bind the form parameters
	 * to the IngredientCommand object, this is done automatically by the 
	 * naming convention of the java objects and the form properties.
	 * 
	 * When the HTTP payload is read from the post request body the @ModelAttribute makes directly 
	 * available to the controller method. The model to view interaction is handled by the 
	 * Dispatcher Servlet.	 
	 */
    public String saveOrUpdate(@ModelAttribute IngredientCommand command){
    	
    	/* 
    	 * A new implementation is passed by the recipeService objects. It is this step that we bind 
    	 * the view properties, the Command objects and the Entity objects together and convert it to a
    	 * Hibernate object that get persisted to the database and passed back to the view to be displayed. 
    	 */
        IngredientCommand savedCommand = ingredientService.saveIngredientCommand(command);

        log.debug("saved receipe id:" + savedCommand.getRecipeId());
        log.debug("saved ingredient id:" + savedCommand.getId());

        return "redirect:/recipe/" + savedCommand.getRecipeId() + "/ingredient/" + savedCommand.getId() + "/show";
    }

    @GetMapping("recipe/{recipeId}/ingredient/{id}/delete")
    public String deleteIngredient(@PathVariable String recipeId,
                                   @PathVariable String id){

        log.debug("deleting ingredient id:" + id);
        ingredientService.deleteById(Long.valueOf(recipeId), Long.valueOf(id));

        return "redirect:/recipe/" + recipeId + "/ingredients";
    }

}
