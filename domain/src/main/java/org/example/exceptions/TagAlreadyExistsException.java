package org.example.exceptions;

public class TagAlreadyExistsException extends RuntimeException {

    public TagAlreadyExistsException(String tag, String recipe) {
        super("Tag: " + tag + " already exists for: " + recipe);
    }
}
