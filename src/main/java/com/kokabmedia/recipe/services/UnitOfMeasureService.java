package com.kokabmedia.recipe.services;

import com.kokabmedia.recipe.commands.UnitOfMeasureCommand;

import java.util.Set;

/*
 * Interface for service layer object.
 * 
 * The practise of coding against an interface implements loose coupling with
 * the @Autowired annotation allowing dependency injection and better unit testing.
 */
public interface UnitOfMeasureService {

    Set<UnitOfMeasureCommand> listAllUoms();
}