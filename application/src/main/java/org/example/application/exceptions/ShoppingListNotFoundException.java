package org.example.application.exceptions;

public class ShoppingListNotFoundException extends RuntimeException {

    public ShoppingListNotFoundException(String name) {
        super("Shopping list with name was not found in system: " + name);
    }
}
