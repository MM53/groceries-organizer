package org.example.aggregates;

import org.example.entities.ItemName;
import org.example.exceptions.RemoveDefaultNameException;
import org.example.units.UnitType;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Item {

    private String id;
    private Set<ItemName> names;
    private UnitType unitType;

    public Item(String id, Set<ItemName> names, UnitType unitType) {
        this.id = id;
        this.names = new HashSet<>(names);
        if (names.stream().noneMatch(itemName -> itemName.getName().equals(id))) {
            this.names.add(new ItemName(id, id));
        }
        this.unitType = unitType;
    }

    public Item(String id, UnitType unitType) {
        this(id, new HashSet<>(List.of(new ItemName(id, id))), unitType);
    }

    public void addAlternativeName(ItemName name) {
        if (name.getItemReference().equals(this.id)) {
            names.add(name);
        }
    }

    public void addAlternativeName(String name) {
        names.add(new ItemName(name, this.id));
    }

    public void removeAlternativeName(String name) {
        if (name.equals(this.id)) {
            throw new RemoveDefaultNameException(this.id);
        }
        names.removeIf(itemName -> itemName.getName().equals(name));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }

    public Set<ItemName> getNames() {
        return names.stream()
                    .map(ItemName::copy)
                    .collect(Collectors.toSet());
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
