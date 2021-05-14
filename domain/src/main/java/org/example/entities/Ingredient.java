package org.example.entities;

import org.example.valueObjects.Amount;

import java.util.Objects;
import java.util.UUID;

public class Ingredient {

    private final UUID id;
    private final String recipeReference;
    private final String itemReference;
    private Amount amount;

    public Ingredient(String recipeReference, String itemReference, Amount amount) {
        this(UUID.randomUUID(), recipeReference, itemReference, false, amount);
    }
    public Ingredient(String recipeReference, String itemReference, boolean bought, Amount amount) {
        this(UUID.randomUUID(), recipeReference, itemReference, bought, amount);
    }

    public Ingredient(UUID id, String recipeReference, String itemReference, boolean bought, Amount amount) {
        this.id = id;
        this.recipeReference = recipeReference;
        this.itemReference = itemReference;
        this.amount = amount;
    }

    public UUID getId() {
        return id;
    }

    public String getRecipeReference() {
        return recipeReference;
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
