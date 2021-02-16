package org.example.entities.aggregateRoots;

import org.example.entities.ItemName;
import org.example.units.UnitTypes;

import java.util.*;

public class Item {

    private String id;
    private Set<ItemName> names;
    private UnitTypes unitType;

    public Item(String id, Set<ItemName> names, UnitTypes unitType) {
        this.id = id;
        this.names = new HashSet<>(names);
        if (names.stream().noneMatch(itemName -> itemName.getName().equals(id))) {
            this.names.add(new ItemName(id, id));
        }
        this.unitType = unitType;
    }

    public Item(String id, UnitTypes unitType) {
        this(id, new HashSet<>(List.of(new ItemName(id, id))), unitType);
    }

    public void addAlternativeName(ItemName name) {
        names.add(name);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UnitTypes getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitTypes unitType) {
        this.unitType = unitType;
    }

    public Set<ItemName> getNames() {
        return names;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return id.equals(item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
