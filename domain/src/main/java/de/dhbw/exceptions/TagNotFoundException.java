package de.dhbw.exceptions;

public class TagNotFoundException extends RuntimeException {

    public TagNotFoundException(String tag, String recipe) {
        super("Could not find tag: " + tag + " in: " + recipe);
    }
}
