package com.kokabmedia.recipe.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/*
 * This class will handle the modelling of the database tables and also function as model for 
 * communication between the database and this application.
 * 
 * It will also act as a model class for values bounded to the HTML view file properties.
 * 
 * Getter and Setter methods and constructors of this class is provided with the Lombok framework. 
 * Getter setter methods and  constructors will be automatically generated by Lombok framework 
 * under the hood.
 */
@Getter
@Setter
/* 
 * The @Entity annotation from javax.persistence enables the JPA framework to manage 
 * the Recipe class as a JPA entity. The Recipe class is an entity and will be  mapped to a 
 * database table with the name Recipe. 
 * 
 * The @Entity annotation will automatically with Hibernate, JPA and Spring auto 
 * configuration create a Recipe table in the database.
 */
@Entity
public class Recipe {

	/*
	 * The @Id annotation makes this field a primary key in the database table.
	 * 
	 * The @GeneratedValue annotation makes the Hibernate generate the primary key value.
	 * 
	 * The GenerationType.IDENTITY parameter indicates that the id will be generated by the
	 * database.
	 * 
	 * Primary key will uniquely identify each row in a database table.
	 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private Integer prepTime;
    private Integer cookTime;
    private Integer servings;
    private String source;
    private String url;

    /*
     * @Lob annotation Specifies that a persistent property or field should be persisted as a large object 
     * to a database-supported large object type.
     */
    @Lob
    private String directions;

	/* 
	 * This field is for relation mapping purposes, a recipe can have a list of
	 * multiple ingredients.
	 * 
	 * The @OneToMany annotation indicates that Recipe has one to many relationship 
	 * mapping with Ingredient, one recipe can have multiple ingredients. The many side of the
	 * relationship will be the owning side. 
	 * 
	 * The mappedBy parameter is describing which table will be owning the relationship,
	 * The Ingredient table will have a recipe_id column with a foreign key value and will 
	 * be owning the relationship. The recipe_id column in the Ingredient table will have a 
	 * link to specific row in the Recipe table. Multiple ingredients can be associated with
	 * the same recipe.
	 * 
	 * The mappedBy parameter is set to the non owning side of the relationship. The
	 * mappedBy parameter makes sure that a ingridient_id column is not created in the 
	 * Recipe table with a foreign key value.
	 * 
	 * The fetch strategy for the OneToMany side of the relations is Lazy Fetch. We can 
	 * change the the fetch type parameter FetchType.Eager this will change the fetching 
	 * strategy of the Ingredient entity so that the fetch type is Eager and Hibernate will fetch 
	 * the Ingredient data when fetching the Recipe data.
	 * 
	 * The CascadeType.ALL parameter control how changes are cascaded from parent object to 
	 * child objects, if a Recipe row is deleted the all Ingredients that are associated with 
	 * that Recipe will all so be deleted.
	 */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "recipe")
    private Set<Ingredient> ingredients = new HashSet<>();

    /*
     * @Lob annotation Specifies that a persistent property or field should be persisted as a large object 
     * to a database-supported large object type. This is needed for images.
     */
    @Lob
    private Byte[] image;

    /*
     * The @Enumerated persist the Difficulty Enum objects and maps it as an enum value to and from 
     * its database representation, this is done with JPA.
     */
    @Enumerated(value = EnumType.STRING)
    private Difficulty difficulty;

    /*
	 * Creates a OneToOne relationship mapping with the @OneToOne annotation, Recipe can have
	 * one Notes and one Notes can be associated with one Recipe.
	 *  
	 * JPA and Hibernate will with the @OneToOne annotation on this field create a notes_id 
	 * column with a foreign key value in the Recipe table. The notes_id column will link to a 
	 * specific row in the Notes table. 
	 * 
	 * Notes and Recipe have FetchType.Eager by default.
	 * 
	 * Recipe has a bidirectional association with Notes.
	 */
    @OneToOne(cascade = CascadeType.ALL)
    private Notes notes;

    /*
	 * The @ManyToMany annotation indicates that Category can have many Recipe and a Recipe
	 * can be associated with many Categories. Recipe can have a list of multiple Categories.
	 * 
	 * The @ManyToMany annotation creates a new recipe__category joint table in the  
	 * database with recipe_id and category_id columns with foreign key values. This entity
	 * is the owning side of the relationship because it has the @JoinTable annotation. 
	 * 
	 * The fetch strategy for the ManyToMany side of the relations is Lazy Fetch.
	 */
    @ManyToMany
    /* 
     * The @JointTable annotation is added to the owning side of the relationship, it lets us
	 * define the name of the join table and the join column and the inverse join column.
	 */
    @JoinTable(name = "recipe_category",
        joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    public void setNotes(Notes notes) {
        if (notes != null) {
            this.notes = notes;
            notes.setRecipe(this);
        }
    }

    public Recipe addIngredient(Ingredient ingredient){
        ingredient.setRecipe(this);
        this.ingredients.add(ingredient);
        return this;
    }
}