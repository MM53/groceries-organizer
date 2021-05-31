package de.dhbw.exceptions;

public class ShoppingListItemAlreadyExistsException extends RuntimeException {

    public ShoppingListItemAlreadyExistsException(String name) {
        super("List item with reference to: " + name + " already exists");
    }
}
