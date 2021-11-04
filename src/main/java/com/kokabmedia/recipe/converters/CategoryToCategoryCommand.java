package com.kokabmedia.recipe.converters;

import com.kokabmedia.recipe.commands.CategoryCommand;
import com.kokabmedia.recipe.domain.Category;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/*
* The @Component annotation allows the Spring framework to creates an instance (bean) 
* of this class and manage it with the Spring Application Context (the IOC container)
* that maintains all the beans for the application.  
*
* The @Component annotation lets the Spring framework manage class as a Spring bean. 
* The Spring framework will find the bean with auto-detection when scanning the class 
* path with component scanning. It turns the class into a Spring bean at the auto-scan 
* time.
* 
* @Component annotation allows the CategoryToCategoryCommand class and to be wired in as 
* dependency to a another object or a bean with the @Autowired annotation.
*/
@Component
/*
 * The convert classes converts Entity (Domain) objects to Command objects and Command 
 * objects back to Entity objects 
 * 
 * The Spring interface Converter takes in two types and with the implemented convert() method
 * takes one type and converts it to the other type and returns that object.
 */
public class CategoryToCategoryCommand implements Converter<Category, CategoryCommand> {
	
	@Synchronized
    @Nullable
    @Override
    public CategoryCommand convert(Category source) {
        if (source == null) {
            return null;
        }

        final CategoryCommand categoryCommand = new CategoryCommand();

        categoryCommand.setId(source.getId());
        categoryCommand.setDescription(source.getDescription());

        return categoryCommand;
    }
	

}
