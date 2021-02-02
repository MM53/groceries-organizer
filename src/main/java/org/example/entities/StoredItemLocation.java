package org.example.entities;

import org.example.units.Unit;
import org.example.valueObjects.Amount;
import org.example.valueObjects.Location;

import java.util.UUID;

public class StoredItemLocation {

    private final UUID id;
    private final Location location;
    private Amount amount;

    public StoredItemLocation(Location location, Amount amount) {
        id = UUID.randomUUID();
        this.location = location;
        this.amount = amount;
    }

    public UUID getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }
}
