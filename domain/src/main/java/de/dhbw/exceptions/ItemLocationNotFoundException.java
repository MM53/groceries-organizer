package de.dhbw.exceptions;

import java.util.UUID;

public class ItemLocationNotFoundException extends RuntimeException {

    public ItemLocationNotFoundException(UUID itemLocationId, UUID storedItemId) {
        super("Could not find itemLocation with id: " + itemLocationId.toString() + " on: " + storedItemId.toString());
    }
}
