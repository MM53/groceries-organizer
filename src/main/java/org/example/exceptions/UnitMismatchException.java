package org.example.exceptions;

import org.example.units.UnitType;

public class UnitMismatchException extends Exception{

    private final UnitType expected;
    private final UnitType actual;

    public UnitMismatchException(UnitType expected, UnitType actual) {
        this.expected = expected;
        this.actual = actual;
    }

    public UnitMismatchException(String message, UnitType expected, UnitType actual) {
        super(message);
        this.expected = expected;
        this.actual = actual;
    }
}
