package org.example.services;

import org.example.units.Unit;
import org.example.valueObjects.Amount;

import java.util.List;

public class AmountService {

    public Amount addAmounts(List<Amount> amounts) {
        return amounts.stream()
                      .reduce(this::addAmounts)
                      .get();
    }

    public Amount addAmounts(Amount amount, Amount amount2) {
        if (amount.getUnit().getType().equals(amount2.getUnit().getType())) {
            return new Amount(amount.getValue() + amount2.getValueInUnit(amount.getUnit()), amount.getUnit());
        }
        throw new RuntimeException("Error");
    }

    public Amount subAmounts(Amount amount, Amount amount2) {
        if (amount.getUnit().getType().equals(amount2.getUnit().getType())) {
            return new Amount(amount.getValue() - amount2.getValueInUnit(amount.getUnit()), amount.getUnit());
        }
        throw new RuntimeException("Error");
    }
}
