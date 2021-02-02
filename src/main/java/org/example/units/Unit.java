package org.example.units;

public interface Unit {
    public double getFactor();
    public String getSymbol();
    public default UnitTypes getType() {
        return UnitTypes.valueOf(this.getClass()
                                     .getSimpleName()
                                     .toUpperCase());
    }
}
