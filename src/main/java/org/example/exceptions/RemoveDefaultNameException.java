package org.example.exceptions;

public class RemoveDefaultNameException extends RuntimeException {

    public RemoveDefaultNameException(String itemName) {
        super("Cannot remove default name from: " + itemName);
    }
}
