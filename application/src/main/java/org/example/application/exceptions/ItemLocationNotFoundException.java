package org.example.application.exceptions;

public class ItemLocationNotFoundException extends RuntimeException {

    public ItemLocationNotFoundException(String location, String itemName) {
        super("Could not find location: " + location + " for item: " + itemName);
    }
}
