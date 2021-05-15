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

    //TODO
    public ItemLocation getItemLocation(UUID itemLocationId) {
        return itemLocations.stream()
                            .filter(i -> i.getId().equals(itemLocationId))
                            .findAny()
                            .orElseThrow();
    }

    public void addItemLocation(ItemLocation newLocation) {
        itemUtilService.validate(itemReference, newLocation.getAmount().getUnit().getType());

        if (!itemLocations.add(newLocation)) {
            throw new RuntimeException();
        }
    }

    public void addItemLocation(Location location, Amount amount) {
        addItemLocation(new ItemLocation(this.id, location, amount));
    }

    public void updateItemLocationAmount(UUID itemLocationId, Amount amount) {
        itemUtilService.validate(itemReference, amount.getUnit().getType());

        ItemLocation itemLocation = getItemLocation(itemLocationId);
        itemLocation.setAmount(itemLocation.getAmount().add(amount));
    }

    public void removeLocation(UUID itemLocationId) {
        itemLocations.removeIf(itemLocation -> itemLocation.getId().equals(itemLocationId));
    }

    public Amount getTotalAmount() {
        return itemLocations.stream()
                            .map(ItemLocation::getAmount)
                            .reduce(Amount::add)
                            .orElse(new Amount(0, itemUtilService.getUnit(itemReference)));
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
