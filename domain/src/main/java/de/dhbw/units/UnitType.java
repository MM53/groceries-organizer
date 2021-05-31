package de.dhbw.units;

public enum UnitType {
    WEIGHT(Weight.GRAM),
    VOLUME(Volume.LITER),
    PIECES(Pieces.PIECES);

    private final Unit base;

    UnitType(Unit base) {
        this.base = base;
    }

    public Unit getBase() {
        return base;
    }
}
