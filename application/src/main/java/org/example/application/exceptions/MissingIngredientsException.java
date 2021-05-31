package org.example.application.exceptions;

import org.example.aggregates.Recipe;
import org.example.entities.Ingredient;

public class MissingIngredientsException extends RuntimeException {

    public MissingIngredientsException(Recipe recipe) {
        super("Some ingredients are missing to cook: " + recipe.getName());
    }

    public MissingIngredientsException(Ingredient ingredient) {
        super("Some ingredients are missing to cook: " + ingredient.getItemReference());
    }
}
