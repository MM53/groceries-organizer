package de.dhbw.application.exceptions;

public class RemoveDefaultShoppingListException extends RuntimeException {

    public RemoveDefaultShoppingListException() {
        super("Cannot remove default shopping-list");
    }
}
