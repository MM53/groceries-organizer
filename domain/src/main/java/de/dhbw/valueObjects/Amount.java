package de.dhbw.valueObjects;

import de.dhbw.exceptions.UnitMismatchException;
import de.dhbw.units.Unit;

import java.text.DecimalFormat;
import java.util.Objects;

public final class Amount {
    private final double value;
    private final Unit unit;

    public Amount(double value, Unit unit) {
        this.value = value;
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public double getValueInUnit(Unit targetUnit) {
        validateUnits(targetUnit);
        return (value * unit.getFactor()) / targetUnit.getFactor();
    }

    public Unit getUnit() {
        return unit;
    }

    public boolean isMoreThan(Amount comparedAmount) {
        validateUnits(comparedAmount);
        return value > comparedAmount.getValueInUnit(unit);
    }

    public boolean isEmpty() {
        return value == 0;
    }

    public Amount add(Amount addend) {
        validateUnits(addend);
        return new Amount(value + addend.getValueInUnit(unit), unit);
    }

    public Amount sub(Amount subtrahend) {
        validateUnits(subtrahend);
        return new Amount(value - subtrahend.getValueInUnit(unit), unit);
    }

    private void validateUnits(Unit unitToCompare) {
        if (!unit.getType().equals(unitToCompare.getType())) {
            throw new UnitMismatchException(unit.getType(), unitToCompare.getType());
        }
    }

    private void validateUnits(Amount comparedAmount) {
        validateUnits(comparedAmount.getUnit());
    }

    @Override
    public String toString() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(getValue()) + unit.getSymbol();
    }

    @Override
    public boolean equals(Object o) {
        Unit baseUnit = unit.getType().getBase();
        if (this == o) return true;
        if (!(o instanceof Amount)) return false;
        Amount amount = (Amount) o;
        return Double.compare(amount.getValueInUnit(baseUnit), getValueInUnit(baseUnit)) == 0 &&
               amount.getUnit().getType().getBase().equals(baseUnit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, unit);
    }
}
