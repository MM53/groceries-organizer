package org.example.application.exceptions;

public class RemoveDefaultShoppingListException extends RuntimeException {

    public RemoveDefaultShoppingListException() {
        super("Cannot remove default shopping-list");
    }
}
