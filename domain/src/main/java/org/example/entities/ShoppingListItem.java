package org.example.entities;

import org.example.valueObjects.Amount;

import java.util.Objects;
import java.util.UUID;

public class ShoppingListItem {

    private final UUID id;
    private final String itemReference;
    private boolean bought;
    private Amount amount;

    public ShoppingListItem(String itemReference, Amount amount) {
        this(UUID.randomUUID(), itemReference, false, amount);
    }
    public ShoppingListItem(String itemReference, boolean bought, Amount amount) {
        this(UUID.randomUUID(), itemReference, bought, amount);
    }

    public ShoppingListItem(UUID id, String itemReference, boolean bought, Amount amount) {
        this.id = id;
        this.itemReference = itemReference;
        this.amount = amount;
        this.bought = bought;
    }

    public UUID getId() {
        return id;
    }

    public String getItemReference() {
        return itemReference;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public ShoppingListItem copy() {
        return new ShoppingListItem(id, itemReference, bought, amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingListItem that = (ShoppingListItem) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
