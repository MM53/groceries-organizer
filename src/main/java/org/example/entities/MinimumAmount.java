package org.example.entities;

import org.example.valueObjects.Amount;

import java.util.Objects;

public class MinimumAmount {

    private String itemName;
    private Amount amount;

    public MinimumAmount() {
    }

    public MinimumAmount(String itemName, Amount amount) {
        this.itemName = itemName;
        this.amount = amount;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MinimumAmount that = (MinimumAmount) o;
        return Objects.equals(itemName, that.itemName) && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemName, amount);
    }
}
