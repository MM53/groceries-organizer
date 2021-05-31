package de.dhbw.entities;

import java.util.Objects;

public class ItemName {

    private String name;
    private String itemReference;

    public ItemName(String name, String itemReference) {
        this.name = name;
        this.itemReference = itemReference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemReference() {
        return itemReference;
    }

    public void setItemReference(String itemReference) {
        this.itemReference = itemReference;
    }

    public ItemName copy() {
        return new ItemName(name, itemReference);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemName)) return false;
        ItemName itemName = (ItemName) o;
        return name.equals(itemName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
