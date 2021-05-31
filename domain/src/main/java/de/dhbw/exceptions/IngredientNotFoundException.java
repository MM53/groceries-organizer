package de.dhbw.exceptions;

import java.util.UUID;

public class IngredientNotFoundException extends RuntimeException {

    public IngredientNotFoundException(UUID  ingredientId, String recipe) {
        super("Could not find ingredient with id: " + ingredientId.toString() + " in: " + recipe);
    }
}
