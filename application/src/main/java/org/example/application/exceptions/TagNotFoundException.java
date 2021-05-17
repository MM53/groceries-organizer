package org.example.application.exceptions;

public class TagNotFoundException extends RuntimeException {

    public TagNotFoundException(String name) {
        super("Tag with name was not found in system: " + name);
    }


}
