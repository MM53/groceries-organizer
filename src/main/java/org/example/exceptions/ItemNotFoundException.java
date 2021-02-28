package org.example.exceptions;

public class ItemNotFoundException extends Exception {

    public ItemNotFoundException(String itemName) {
        super("Could not find item with name: " + itemName);
    }
}
