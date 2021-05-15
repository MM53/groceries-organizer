package org.example.exceptions;

import java.util.UUID;

public class IngredientAlreadyExistsException extends RuntimeException {

    public IngredientAlreadyExistsException(UUID  ingredientId, String recipe) {
        super("Ingredient with id: " + ingredientId.toString() + " already exists for: " + recipe);
    }
}
