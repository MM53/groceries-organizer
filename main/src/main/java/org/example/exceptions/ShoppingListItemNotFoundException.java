package org.example.exceptions;

public class ShoppingListItemNotFoundException extends RuntimeException {

    public ShoppingListItemNotFoundException(String name) {
        super("Could not find entry with name: " + name);
    }
}
