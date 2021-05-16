package org.example.adapter;

import org.example.units.Pieces;
import org.example.units.Unit;
import org.example.units.Volume;
import org.example.units.Weight;
import org.example.valueObjects.Amount;

public class AmountAdapter {

    public static Amount ExtractFromString(String amountString) {
        double value = Double.parseDouble(amountString.replaceAll("[^0-9]", ""));
        Unit unit = switch (amountString.replaceAll("[0-9]", "")) {
            case "g" -> Weight.GRAM;
            case "kg" -> Weight.KILOGRAM;
            case "l" -> Volume.LITER;
            case "ml" -> Volume.MILLILITER;
            case "" -> Pieces.PIECES;
            default -> throw new RuntimeException();
        };
        return new Amount(value, unit);
    }
}