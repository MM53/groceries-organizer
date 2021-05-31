package de.dhbw.units;

public enum Volume implements Unit {
    LITER(1, "l"),
    MILLILITER(0.001, "ml");

    private final double factor;
    private final String symbol;

    Volume(double factor, String symbol) {
        this.factor = factor;
        this.symbol = symbol;
    }

    @Override
    public double getFactor() {
        return factor;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

}
