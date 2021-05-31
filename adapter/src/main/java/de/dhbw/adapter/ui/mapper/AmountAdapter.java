package de.dhbw.adapter.ui.mapper;

import de.dhbw.adapter.ui.exceptions.NoUnitFoundException;
import de.dhbw.units.Pieces;
import de.dhbw.units.Unit;
import de.dhbw.units.Volume;
import de.dhbw.units.Weight;
import de.dhbw.valueObjects.Amount;

public class AmountAdapter {

    public static Amount ExtractFromString(String amountString) {
        double value = Double.parseDouble(amountString.replaceAll("[^0-9]", ""));
        Unit unit = switch (amountString.replaceAll("[0-9]", "").replaceAll(" ", "")) {
            case "g" -> Weight.GRAM;
            case "kg" -> Weight.KILOGRAM;
            case "l" -> Volume.LITER;
            case "ml" -> Volume.MILLILITER;
            case "" -> Pieces.PIECES;
            default -> throw new NoUnitFoundException(amountString);
        };
        return new Amount(value, unit);
    }
}
