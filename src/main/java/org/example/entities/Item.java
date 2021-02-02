package org.example.entities;

import org.example.units.UnitTypes;

import java.util.ArrayList;
import java.util.List;

public class Item {

    private final String primaryName;
    private final List<String> alternativeNames;
    private final UnitTypes unitType;

    public Item(String primaryName, List<String> alternativeNames, UnitTypes unitType) {
        this.primaryName = primaryName;
        this.alternativeNames = alternativeNames;
        this.unitType = unitType;
    }

    public void addAlternativeName(String name) {
        alternativeNames.add(name);
    }

    public List<String> getAllNames() {
        List<String> names = new ArrayList<>(alternativeNames);
        names.add(0, primaryName);
        return names;
    }

    public String getPrimaryName() {
        return primaryName;
    }

    public List<String> getAlternativeNames() {
        return alternativeNames;
    }

    public UnitTypes getUnitType() {
        return unitType;
    }
}
