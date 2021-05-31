package de.dhbw.units;

public interface Unit {
    public double getFactor();
    public String getSymbol();
    public String name();
    public default UnitType getType() {
        return UnitType.valueOf(this.getClass()
                                    .getSimpleName()
                                    .toUpperCase());
    }
}
