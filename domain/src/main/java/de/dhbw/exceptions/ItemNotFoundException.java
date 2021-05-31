package de.dhbw.exceptions;

public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(String itemName) {
        super("Could not find item with name: " + itemName);
    }
}
