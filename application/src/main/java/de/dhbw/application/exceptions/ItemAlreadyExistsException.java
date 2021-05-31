package de.dhbw.application.exceptions;

public class ItemAlreadyExistsException extends RuntimeException {

    public ItemAlreadyExistsException(String itemName) {
        super("Item with name already exists in system: " + itemName);
    }
}
