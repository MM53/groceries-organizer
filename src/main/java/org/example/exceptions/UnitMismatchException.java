package org.example.exceptions;

import org.example.units.UnitTypes;

public class UnitMismatchException extends Exception{

    private final UnitTypes expected;
    private final UnitTypes actual;

    public UnitMismatchException(UnitTypes expected, UnitTypes actual) {
        this.expected = expected;
        this.actual = actual;
    }

    public UnitMismatchException(String message, UnitTypes expected, UnitTypes actual) {
        super(message);
        this.expected = expected;
        this.actual = actual;
    }
}
