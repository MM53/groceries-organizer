package org.example.adapter.ui.exceptions;

public class NoUnitFoundException extends RuntimeException{

    public NoUnitFoundException(String input) {
        super("Could not parse unit from: " + input);
    }
}
