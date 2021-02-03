package org.example.persistence.entities;

import org.example.units.Pieces;
import org.example.units.UnitTypes;
import org.example.units.Volume;
import org.example.units.Weight;
import org.example.valueObjects.Amount;
import org.example.valueObjects.Location;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;
import java.util.UUID;

@Entity
public class ItemLocationEntityJava {

    @Id
    @GeneratedValue
    private UUID id;

    private String locationRoom;
    private String locationPlace;
    private String locationShelf;

    private UnitTypes amountUnitType;
    private Double amountValue;
    private String amountUnit;

    public ItemLocationEntityJava() {
    }

    public Location getLocation() {
        return new Location(locationRoom, locationPlace, locationShelf);
    }

    public void setLocation(Location location) {
        locationPlace = location.getPlace();
        locationRoom = location.getRoom();
        locationShelf = location.getShelf();
    }

    public Amount getAmount() {
        return switch (amountUnitType) {
            case PIECES -> new Amount(amountValue, Pieces.PIECES);
            case VOLUME -> new Amount(amountValue, Volume.valueOf(amountUnit));
            case WEIGHT -> new Amount(amountValue, Weight.valueOf(amountUnit));
        };
    }

    public void setAmount(Amount amount) {
        amountUnitType = amount.getUnit().getType();
        amountValue = amount.getValue();
        amountUnit = amount.getUnit().name();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLocationRoom() {
        return locationRoom;
    }

    public void setLocationRoom(String locationRoom) {
        this.locationRoom = locationRoom;
    }

    public String getLocationPlace() {
        return locationPlace;
    }

    public void setLocationPlace(String locationPlace) {
        this.locationPlace = locationPlace;
    }

    public String getLocationShelf() {
        return locationShelf;
    }

    public void setLocationShelf(String locationShelf) {
        this.locationShelf = locationShelf;
    }

    public UnitTypes getAmountUnitType() {
        return amountUnitType;
    }

    public void setAmountUnitType(UnitTypes amountUnitType) {
        this.amountUnitType = amountUnitType;
    }

    public Double getAmountValue() {
        return amountValue;
    }

    public void setAmountValue(Double amountValue) {
        this.amountValue = amountValue;
    }

    public String getAmountUnit() {
        return amountUnit;
    }

    public void setAmountUnit(String amountUnit) {
        this.amountUnit = amountUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemLocationEntityJava that = (ItemLocationEntityJava) o;
        return Objects.equals(id, that.id) && Objects.equals(locationRoom, that.locationRoom) && Objects.equals(locationPlace, that.locationPlace) && Objects.equals(locationShelf, that.locationShelf) && amountUnitType == that.amountUnitType && Objects.equals(amountValue, that.amountValue) && Objects.equals(amountUnit, that.amountUnit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, locationRoom, locationPlace, locationShelf, amountUnitType, amountValue, amountUnit);
    }
}
