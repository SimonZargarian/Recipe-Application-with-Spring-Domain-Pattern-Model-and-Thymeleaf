package com.kokabmedia.recipe.controllers;

import com.kokabmedia.recipe.commands.RecipeCommand;
import com.kokabmedia.recipe.exceptions.NotFoundException;
import com.kokabmedia.recipe.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
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
* @Controller annotation allows the IndexController class and to be wired in as dependency 
* to a another object or a bean with the @Autowired annotation.
* 
* The @Controller annotation is a specialisation of @Component annotation for more specific 
* use cases.
*/
@Controller
public class RecipeController {
	
	private static final String RECIPE_RECIPEFORM_URL = "recipe/recipeform";
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }
    
    /*
   	 * This method returns an user with a specific id 
   	 * 
   	 * When a GET HTTP request is sent to a certain URL and that URL contains a path which
   	 * is declared on the @GetMapping annotation, in this case the appended "/recipe/{id}/show",
   	 * this method will be called.
   	 * 
   	 * The @GetMapping annotation will bind and make showById() method respond to a HTTP GET
   	 * request.
   	 * 
   	 * The ("recipe/{id}/show") parameter allows the method to read the appended String after 
   	 * the URL http://localhost:8080/recipe/ as a path variable that is attached, so when a 
   	 * int is appended after http://localhost:8080/recipe/ with a GET HTTP request the 
   	 * retrieveUser(PathVariable String id) method is called. 
   	 * 
   	 * The name of the "/{id}" parameter must match the @PathVariable annotation argument 
   	 * String id.
   	 */
    @GetMapping("/recipe/{id}/show")
    /*
     * The @PathVariable annotation will make the path variable in the URL available
	 * for this showById() method via the method argument. When a String id is
	 * appended to http://localhost:8080/recipe/ it can be handled by the showById()
	 * method.
	 */
    public String showById(@PathVariable String id, Model model){

    	/* 
		 * The "recipe" attribute will be mapped to variable in the JSP file that contains the variable "recipe"
		 * and a recipe  will be shown coming in from the second parameter value. The model instance will
		 * make it available to the JSP view file.
		 */
        model.addAttribute("recipe", recipeService.findById(new Long(id)));

        return "recipe/show";
    }

    @GetMapping("recipe/new")
    public String newRecipe(Model model){
        model.addAttribute("recipe", new RecipeCommand());

        return "recipe/recipeform";
    }

    @GetMapping("recipe/{id}/update")
    public String updateRecipe(@PathVariable String id, Model model){
        model.addAttribute("recipe", recipeService.findCommandById(Long.valueOf(id)));
        return RECIPE_RECIPEFORM_URL;
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
    @PostMapping("recipe")
    /*
     * The @Valid annotation enables validation on User class
     *      
	 * The "recipe" attribute will be mapped to variable in the HTML file recipeform.html 
	 * that contains the Thymeleaf objects reference "recipe" with a reference fields that
	 * correspond to the Command object.  
	 * 
	 * The RecipeCommand object will be mapped to the form (view) with @ModelAttribute parameter 
	 * annotation and make the data available to and from the form.
	 * 
	 * The @ModelAttribute parameter annotation will bind the form parameters to the RecipeCommand 
	 * object, this is done automatically by the  naming convention of the java objects and the form 
	 * properties.
	 * 
	 * When the HTTP payload is read from the post request body the @ModelAttribute makes directly 
	 * available to the controller method. The model to view interaction is handled by the Dispatcher
	 * Servlet.
	 */
    public String saveOrUpdate(@Valid @ModelAttribute("recipe") RecipeCommand command, BindingResult bindingResult){

        if(bindingResult.hasErrors()){

            bindingResult.getAllErrors().forEach(objectError -> {
                log.debug(objectError.toString());
            });

            return RECIPE_RECIPEFORM_URL;
        }

    	/* 
    	 * A new implementation is passed by the recipeService objects. It is this step that we bind 
    	 * the view properties, the Command objects and the Entity objects together and convert it to a
    	 * Hibernate object that get persisted to the database and passed back to the view to be displayed. 
    	 */
        RecipeCommand savedCommand = recipeService.saveRecipeCommand(command);

        // returns the view with the name "recipe"
        return "redirect:/recipe/" + savedCommand.getId() + "/show";
    }

    @GetMapping("recipe/{id}/delete")
    public String deleteById(@PathVariable String id){

        log.debug("Deleting id: " + id);

        recipeService.deleteById(Long.valueOf(id));
        return "redirect:/";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFound(Exception exception){

        log.error("Handling not found exception");
        log.error(exception.getMessage());

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("404error");
        modelAndView.addObject("exception", exception);

        return modelAndView;
    }
	

	
}
