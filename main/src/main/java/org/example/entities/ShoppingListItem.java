package org.example.entities;

import org.example.valueObjects.Amount;

import java.util.UUID;

public class ShoppingListItem {

    private final UUID id;
    private final String shoppingListReference;
    private final String itemReference;
    private boolean bought;
    private Amount amount;

    public ShoppingListItem(String shoppingListReference, String itemReference, Amount amount) {
        this(UUID.randomUUID(), shoppingListReference, itemReference, false, amount);
    }
    public ShoppingListItem(String shoppingListReference, String itemReference, boolean bought, Amount amount) {
        this(UUID.randomUUID(), shoppingListReference, itemReference, bought, amount);
    }

    public ShoppingListItem(UUID id, String shoppingListReference, String itemReference, boolean bought, Amount amount) {
        this.id = id;
        this.shoppingListReference = shoppingListReference;
        this.itemReference = itemReference;
        this.amount = amount;
        this.bought = bought;
    }

    public UUID getId() {
        return id;
    }

    public String getShoppingListReference() {
        return shoppingListReference;
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
        if (this.amount == null) {
            this.amount = amount;
        } else {
            this.amount = this.amount.add(amount);
        }
    }
}
