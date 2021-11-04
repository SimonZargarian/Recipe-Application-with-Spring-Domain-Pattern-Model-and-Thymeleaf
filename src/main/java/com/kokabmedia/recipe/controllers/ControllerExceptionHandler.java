package com.kokabmedia.recipe.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

//Enables Lombok to generate a logger field.
@Slf4j
/*
 * This class is designed to handle all exceptions that occur with the controller 
 * methods of this application.
 * 
 * ControllerAdvice means that the content of this class is applicable to all 
 * controllers of this application.
 */
@ControllerAdvice
public class ControllerExceptionHandler {

	   @ResponseStatus(HttpStatus.BAD_REQUEST)
	    @ExceptionHandler(NumberFormatException.class)
	    public ModelAndView handleNumberFormat(Exception exception){

	        log.error("Handling Number Format Exception");
	        log.error(exception.getMessage());

	        ModelAndView modelAndView = new ModelAndView();

	        modelAndView.setViewName("400error");
	        modelAndView.addObject("exception", exception);

	        return modelAndView;
	    }
}
