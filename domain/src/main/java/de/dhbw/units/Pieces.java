package de.dhbw.units;

public class Pieces implements Unit {

    public static final Pieces PIECES = new Pieces();

    private Pieces() {
    }

    @Override
    public double getFactor() {
        return 1;
    }

    @Override
    public String getSymbol() {
        return "";
    }

    @Override
    public String name() {
        return "PIECES";
    }
}
