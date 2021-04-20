package org.example.entities;

import org.example.valueObjects.Amount;

import java.util.UUID;

public class ShoppingListItem {

    private final UUID id;
    private final String itemReference;
    private boolean bought;
    private Amount amount;

    public ShoppingListItem(String itemReference, Amount amount) {
        this(UUID.randomUUID(), itemReference, amount, false);
    }
    public ShoppingListItem(String itemReference, Amount amount, boolean bought) {
        this(UUID.randomUUID(), itemReference, amount, bought);
    }

    public ShoppingListItem(UUID id, String itemReference, Amount amount, boolean bought) {
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

    public void addAmount(Amount amount) {
        this.amount = this.amount.add(amount);
    }
}
