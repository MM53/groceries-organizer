package org.example.units;

public enum UnitTypes {
    WEIGHT(Weight.GRAM),
    VOLUME(Volume.LITER),
    PIECES(Pieces.PIECES);

    private final Unit base;

    UnitTypes(Unit base) {
        this.base = base;
    }

    public Unit getBase() {
        return base;
    }
}
