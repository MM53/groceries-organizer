package org.example.aggregates;

import org.example.entities.ItemLocation;
import org.example.entities.MinimumAmount;
import org.example.exceptions.UnitMismatchException;
import org.example.services.AmountService;
import org.example.units.Weight;
import org.example.valueObjects.Amount;
import org.example.valueObjects.Location;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class StoredItem {

    private final UUID id;
    private final String itemReference;
    private final Set<ItemLocation> itemLocations;
    private final MinimumAmount minimumAmount;

    private final AmountService amountService = new AmountService();

    public StoredItem(String itemReference, Set<ItemLocation> itemLocations, MinimumAmount minimumAmount) {
        this(UUID.randomUUID(), itemReference, itemLocations, minimumAmount);
    }

    public StoredItem(UUID id, String itemReference, Set<ItemLocation> itemLocations, MinimumAmount minimumAmount) {
        this.id = id;
        this.itemReference = itemReference;
        this.itemLocations = itemLocations;
        this.minimumAmount = minimumAmount;
    }

    public void addLocation(ItemLocation itemLocation) throws UnitMismatchException {
//        if (item.getUnitType().equals(itemLocation.getAmount().getUnit().getType())) {
            itemLocations.add(itemLocation);
//        } else {
//            throw new UnitMismatchException(item.getUnitType(), itemLocation.getAmount().getUnit().getType());
//        }
    }

    public void addLocation(Location location, Amount amount) throws UnitMismatchException {
        addLocation(new ItemLocation(this.id, location, amount));
    }

    public void removeLocation(ItemLocation itemLocation) {
        itemLocations.remove(itemLocation);
    }

    public UUID getId() {
        return id;
    }

    public String getItemReference() {
        return itemReference;
    }

    public Set<ItemLocation> getItemLocations() {
        return itemLocations;
    }

    public MinimumAmount getMinimumAmount() {
        return minimumAmount;
    }

    public Amount getTotalAmount() throws RuntimeException {
        return itemLocations.stream()
                            .map(ItemLocation::getAmount)
                            .reduce(amountService::addAmounts)
                            .orElse(new Amount(0, Weight.GRAM));
    }

    public boolean hasEnough(Amount requestedAmount) {
        return getTotalAmount().isMoreThan(requestedAmount);
    }

    public void take(Amount requestedAmount, List<ItemLocation> locations) {
        Iterator<ItemLocation> locationIterator = locations.iterator();
        ItemLocation location;
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
