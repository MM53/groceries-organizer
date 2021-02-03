package org.example.entities;

import org.example.valueObjects.Amount;
import org.example.valueObjects.Location;

import java.util.Objects;
import java.util.UUID;

public class ItemLocation {

    private UUID id;
    private Location location;
    private Amount amount;

    public ItemLocation(Location location, Amount amount) {
        this.location = location;
        this.amount = amount;
    }

    public ItemLocation(UUID id, Location location, Amount amount) {
        this.id = id;
        this.location = location;
        this.amount = amount;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
        ItemLocation that = (ItemLocation) o;
        return Objects.equals(id, that.id) && Objects.equals(location, that.location) && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, location, amount);
    }
}
