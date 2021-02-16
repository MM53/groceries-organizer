package org.example.entities;

import org.example.valueObjects.Amount;

import java.util.Objects;
import java.util.UUID;

public class MinimumAmount {

    private UUID id;
    private Amount amount;

    public MinimumAmount() {
    }

    public MinimumAmount(UUID id, Amount amount) {
        this.id = id;
        this.amount = amount;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
        if (!(o instanceof MinimumAmount)) return false;
        MinimumAmount that = (MinimumAmount) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
