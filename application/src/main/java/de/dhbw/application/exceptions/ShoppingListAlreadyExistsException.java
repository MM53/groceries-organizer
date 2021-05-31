package de.dhbw.application.exceptions;

public class ShoppingListAlreadyExistsException extends RuntimeException {

    public ShoppingListAlreadyExistsException(String name) {
        super("Shopping list with name already exists in system: " + name);
    }
}
