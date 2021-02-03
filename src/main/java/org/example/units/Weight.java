package org.example.units;

public enum Weight implements Unit {
    GRAM(1, "g"),
    KILOGRAM(1000, "kg");

    private final double factor;
    private final String symbol;

    Weight(double factor, String symbol) {
        this.factor = factor;
        this.symbol = symbol;
    }

    public double getFactor() {
        return factor;
    }

    public String getSymbol() {
        return symbol;
    }

}
