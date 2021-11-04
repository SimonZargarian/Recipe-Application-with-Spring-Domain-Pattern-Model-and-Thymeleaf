package com.kokabmedia.recipe.controllers;

import com.kokabmedia.recipe.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
//Causes Lombok to generate a logger field.
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
public class IndexController {

	private final RecipeService recipeService;

	public IndexController(RecipeService recipeService) {
		this.recipeService = recipeService;
	}
	/*
	 * When HTTP request is sent to a certain URL and that URL contains a path which
	 * is declared on the @RequestMapping annotation, in this case the appended root "/",
	 * this method will called and response if needed. 
	 * 
	 * When a GET, POST, PUT or DELETER HTTP request is sent to the URL path with the extension 
	 * "", "/"  or "/index" then the appropriate method in the class will respond.
	 * 	 * 
	 * The dispatcher servlet is the Front Controller for the Spring MVC framework handles all 
	 * the requests of the root (/) of the web application. 
	 * 
	 * Dispatcher servlet knows all the HTTP request methods GET, POST, PUT AND DELETE and what 
	 * java methods they are mapped to with annotations. Dispatcher servlet will delegate which 
	 * controller should handle a specific request. Dispatcher servlet looks at the URL and the 
	 * request method.  
	 */
	@RequestMapping({ "", "/", "/index" })
	public String getIndexPage(Model model) {
		log.debug("Getting Index page");
		
		/* 
		 * The "recipes" attribute will be mapped to variable in the HTML file that contains the variable "recipes"
		 * and a list of recipes will be shown coming in from the second parameter value. The model instance will
		 * make it available to the HTML view file.
		 */
		model.addAttribute("recipes", recipeService.getRecipes());

		return "index";
	}

}
