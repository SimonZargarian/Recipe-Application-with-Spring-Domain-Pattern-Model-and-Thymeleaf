package com.kokabmedia.recipe.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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
@EqualsAndHashCode(exclude = {"recipe"})
/* 
 * The @Entity annotation from javax.persistence enables the JPA framework to manage 
 * the Notes class as a JPA entity. The Notes class is an entity and will be  mapped to a 
 * database table with the name Notes. 
 * 
 * The @Entity annotation will automatically with Hibernate, JPA and Spring auto 
 * configuration create a Notes table in the database.
 */
@Entity
public class Notes {


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

    /*
	 * Creates a OneToOne relationship mapping with the @OneToOne annotation, Recipe can have
	 * one Notes and one Notes can be associated with one Recipe.
	 * 
	 * JPA and Hibernate will with the @OneToOne annotation on this field create a recipe_id 
	 * column with a foreign key value in the Notes table. The recipe_id column will link to a 
	 * specific row in the Recipe table. 
	 * 
	 * Notes and Recipe have FetchType.Eager by default.
	 * 
	 * Notes has bidirectional association with Recipe. OneToOne annotation makes it possible 
	 * to navigate to the Recipe.
	 */
    @OneToOne
    private Recipe recipe;

    /*
     * @Lob annotation Specifies that a persistent property or field should be persisted as a large object 
     * to a database-supported large object type.
     */
    @Lob
    private String recipeNotes;

}