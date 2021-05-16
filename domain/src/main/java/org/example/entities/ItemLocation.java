package org.example.entities;

import org.example.valueObjects.Amount;
import org.example.valueObjects.Location;

import java.util.Objects;
import java.util.UUID;

public class ItemLocation {

    private UUID id;
    private UUID storedItemReference;
    private Location location;
    private Amount amount;

    public ItemLocation(UUID id, Location location, Amount amount) {
        this.id = id;
        this.location = location;
        this.amount = amount;
    }

    public ItemLocation(Location location, Amount amount) {
        this(UUID.randomUUID(), location, amount);
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
        if (!(o instanceof ItemLocation)) return false;
        ItemLocation that = (ItemLocation) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
