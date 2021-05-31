package de.dhbw.application.exceptions;

import de.dhbw.aggregates.Recipe;
import de.dhbw.entities.Ingredient;

public class MissingIngredientsException extends RuntimeException {

    public MissingIngredientsException(Recipe recipe) {
        super("Some ingredients are missing to cook: " + recipe.getName());
    }

    public MissingIngredientsException(Ingredient ingredient) {
        super("Some ingredients are missing to cook: " + ingredient.getItemReference());
    }
}
