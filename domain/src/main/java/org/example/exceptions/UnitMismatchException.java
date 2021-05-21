package org.example.exceptions;

import org.example.units.UnitType;

public class UnitMismatchException extends RuntimeException {

    public UnitMismatchException(UnitType expected, UnitType actual) {
        super("Unit mismatch. Expecting: " + expected + ", got: " + actual);
    }

}
