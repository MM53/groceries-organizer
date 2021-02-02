package org.example.valueObjects;

import org.example.units.Unit;

import java.text.DecimalFormat;

public final class Amount {
    private final double value;
    private final Unit unit;

    public Amount(double value, Unit unit) {
        this.value = value * unit.getFactor();
        this.unit = unit;
    }

    public double getValue() {
        return value / unit.getFactor();
    }

    public double getValueInUnit(Unit targetUnit) {
        return value / targetUnit.getFactor();
    }

    public Unit getUnit() {
        return unit;
    }

    public boolean isMoreThan(Amount amount) {
        return value > amount.getValueInUnit(unit);
    }

    @Override
    public String toString() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(getValue()) + unit.getSymbol();
    }

}
