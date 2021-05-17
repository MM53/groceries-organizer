package org.example.application.exceptions;

import org.example.entities.aggregateRoots.Tag;

import java.util.UUID;

public class RecipeNotFoundException extends RuntimeException {

    public RecipeNotFoundException(String name) {
        super("Recipe with name was not found in system: " + name);
    }

    public RecipeNotFoundException(UUID id) {
        super("Recipe with id was not found in system: " + id.toString());
    }

}
