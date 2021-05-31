package de.dhbw.entities;

import de.dhbw.valueObjects.Amount;

import java.util.Objects;
import java.util.UUID;

public class Ingredient {

    private final UUID id;
    private final String itemReference;
    private Amount amount;

    public Ingredient(String itemReference, Amount amount) {
        this(UUID.randomUUID(), itemReference, amount);
    }

    public Ingredient(UUID id, String itemReference, Amount amount) {
        this.id = id;
        this.itemReference = itemReference;
        this.amount = amount;
    }

    public UUID getId() {
        return id;
    }

    public String getItemReference() {
        return itemReference;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public Ingredient copy() {
        return new Ingredient(id, itemReference, amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
