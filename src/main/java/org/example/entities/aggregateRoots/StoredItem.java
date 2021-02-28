package org.example.entities.aggregateRoots;

import org.example.entities.ItemLocation;
import org.example.entities.MinimumAmount;
import org.example.services.AmountService;
import org.example.services.ItemUtilService;
import org.example.valueObjects.Amount;
import org.example.valueObjects.Location;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class StoredItem {

    private final UUID id;
    private final String itemReference;
    private final Set<ItemLocation> itemLocations;
    private MinimumAmount minimumAmount;

    private final ItemUtilService itemUtilService = new ItemUtilService();

    private final AmountService amountService = new AmountService();

    public StoredItem(String itemReference, Amount minimumAmount) {
        this(UUID.randomUUID(), itemReference, new HashSet<>(), minimumAmount != null ? new MinimumAmount(minimumAmount) : null);
    }

    public StoredItem(UUID id, String itemReference, Set<ItemLocation> itemLocations, MinimumAmount minimumAmount) {
        itemUtilService.validateExistence(itemReference);
        this.id = id;
        this.itemReference = itemReference;
        this.itemLocations = itemLocations;
        this.minimumAmount = minimumAmount;
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

    public void addItemLocation(ItemLocation itemLocation) {
        itemUtilService.validate(itemReference, itemLocation.getAmount().getUnit().getType());
        itemLocations.stream()
                     .filter(location -> location.getLocation().equals(itemLocation.getLocation()))
                     .findAny()
                     .ifPresentOrElse(location -> location.setAmount(location.getAmount().add(itemLocation.getAmount())),
                                      () -> itemLocations.add(itemLocation));
    }

    public void addItemLocation(Location location, Amount amount) {
        addItemLocation(new ItemLocation(this.id, location, amount));
    }

    public void removeLocation(ItemLocation itemLocation) {
        itemLocations.remove(itemLocation);
    }

    public MinimumAmount getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(MinimumAmount minimumAmount) {
        itemUtilService.validate(itemReference, minimumAmount.getAmount().getUnit().getType());
        this.minimumAmount = minimumAmount;
    }

    public void setMinimumAmount(Amount amount) {
        setMinimumAmount(new MinimumAmount(amount));
    }

    public Amount getTotalAmount() throws RuntimeException {
        return itemLocations.stream()
                            .map(ItemLocation::getAmount)
                            .reduce(Amount::add)
                            .orElse(new Amount(0, itemUtilService.getUnit(itemReference)));
    }

    public boolean hasEnough(Amount requestedAmount) {
        return getTotalAmount().isMoreThan(requestedAmount);
    }

    public Amount take(Amount requestedAmount, ItemLocation itemLocation) {
        itemUtilService.validate(itemReference, requestedAmount.getUnit().getType());
        if (requestedAmount.isMoreThan(itemLocation.getAmount())) {
            removeLocation(itemLocation);
            return requestedAmount.sub(itemLocation.getAmount());
        } else {
            itemLocation.setAmount(itemLocation.getAmount().sub(requestedAmount));
            return new Amount(0, requestedAmount.getUnit());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StoredItem)) return false;
        StoredItem that = (StoredItem) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
