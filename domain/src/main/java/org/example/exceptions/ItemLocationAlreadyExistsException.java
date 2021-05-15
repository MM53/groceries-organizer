package org.example.exceptions;

import java.util.UUID;

public class ItemLocationAlreadyExistsException extends RuntimeException {

    public ItemLocationAlreadyExistsException(UUID itemLocationId, UUID storedItemId) {
        super("ItemLocation with id: " + itemLocationId.toString() + " already exists on: " + storedItemId.toString());
    }
}
