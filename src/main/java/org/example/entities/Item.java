package org.example.entities;

import org.example.units.UnitTypes;

import java.util.List;
import java.util.stream.Collectors;

public class Item {

    private String primaryName;
    private List<ItemNameAlternative> alternativeNames;
    private UnitTypes unitType;

    public Item(String primaryName, List<ItemNameAlternative> alternativeNames, UnitTypes unitType) {
        this.primaryName = primaryName;
        this.alternativeNames = alternativeNames;
        this.unitType = unitType;
    }

    public void addAlternativeName(ItemNameAlternative name) {
        alternativeNames.add(name);
    }

    public List<String> getAllNames() {
        List<String> names = alternativeNames.stream()
                                             .map(ItemNameAlternative::getName)
                                             .collect(Collectors.toList());
        names.add(0, primaryName);
        return names;
    }

    public String getPrimaryName() {
        return primaryName;
    }

    public List<ItemNameAlternative> getAlternativeNames() {
        return alternativeNames;
    }

    public UnitTypes getUnitType() {
        return unitType;
    }
}
