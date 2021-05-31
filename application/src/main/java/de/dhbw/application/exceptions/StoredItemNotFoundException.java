package de.dhbw.application.exceptions;

public class StoredItemNotFoundException extends RuntimeException {

    public StoredItemNotFoundException(String itemName) {
        super("Could not find stored item referencing: " + itemName);
    }
}
