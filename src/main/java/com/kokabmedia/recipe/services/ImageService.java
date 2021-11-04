package com.kokabmedia.recipe.services;

import org.springframework.web.multipart.MultipartFile;

/*
 * Interface for service layer object.
 * 
 * The practise of coding against an interface implements loose coupling with
 * the @Autowired annotation allowing dependency injection and better unit testing.
 */
public interface ImageService {

    void saveImageFile(Long recipeId, MultipartFile file);
}