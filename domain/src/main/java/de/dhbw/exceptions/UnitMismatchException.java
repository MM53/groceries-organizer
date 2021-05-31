package de.dhbw.exceptions;

import de.dhbw.units.UnitType;

public class UnitMismatchException extends RuntimeException {

    public UnitMismatchException(UnitType expected, UnitType actual) {
        super("Unit mismatch. Expecting: " + expected + ", got: " + actual);
    }

}
