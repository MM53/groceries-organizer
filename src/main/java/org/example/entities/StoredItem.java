package org.example.entities;

import org.example.exceptions.UnitMismatchException;
import org.example.repositories.StoredItemRepository;
import org.example.services.AmountService;
import org.example.units.Weight;
import org.example.valueObjects.Amount;
import org.example.valueObjects.Location;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class StoredItem {

    private final UUID id;
    private final Item item;
    private final List<StoredItemLocation> locations;

    private final AmountService amountService = new AmountService();

    public StoredItem(UUID id, Item item, List<StoredItemLocation> locations) {
        this.id = id;
        this.item = item;
        this.locations = locations;
    }

    public void addLocation(StoredItemLocation itemLocation) throws UnitMismatchException {
        if (item.getUnitType().equals(itemLocation.getAmount().getUnit().getType())) {
            locations.add(itemLocation);
        } else {
            throw new UnitMismatchException(item.getUnitType(), itemLocation.getAmount().getUnit().getType());
        }
    }

    public void addLocation(Location location, Amount amount) throws UnitMismatchException {
        addLocation(new StoredItemLocation(location, amount));
    }

    public void removeLocation(StoredItemLocation itemLocation) {
        locations.remove(itemLocation);
    }

    public UUID getId() {
        return id;
    }

    public Item getItem() {
        return item;
    }

    public List<StoredItemLocation> getLocations() {
        return locations;
    }

    public Amount getTotalAmount() throws RuntimeException {
        return locations.stream()
                        .map(StoredItemLocation::getAmount)
                        .reduce(amountService::addAmounts)
                        .orElse(new Amount(0, Weight.GRAM));
    }

    public boolean hasEnough(Amount requestedAmount) {
        return getTotalAmount().isMoreThan(requestedAmount);
    }

    public void take(Amount requestedAmount, List<StoredItemLocation> locations) {
        Iterator<StoredItemLocation> locationIterator = locations.iterator();
        StoredItemLocation location;
        Amount requestedAmountLeft = requestedAmount;
        do {
            location = locationIterator.next();
            if (requestedAmountLeft.isMoreThan(location.getAmount())) {
                requestedAmountLeft = amountService.subAmounts(requestedAmount, location.getAmount());
                removeLocation(location);
            } else {
                location.setAmount(amountService.subAmounts(location.getAmount(), requestedAmountLeft));
                break;
            }
        } while (locationIterator.hasNext());

        if (requestedAmountLeft.getValue() > 0) {
            System.out.println("not enough");
        }
    }
}
