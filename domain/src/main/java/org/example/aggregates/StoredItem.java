package org.example.aggregates;

import org.example.entities.ItemLocation;
import org.example.entities.MinimumAmount;
import org.example.exceptions.ItemLocationAlreadyExistsException;
import org.example.exceptions.ItemLocationNotFoundException;
import org.example.services.ItemUtilService;
import org.example.valueObjects.Amount;
import org.example.valueObjects.Location;

import java.util.*;

public class StoredItem {

    private final UUID id;
    private final String itemReference;
    private final Set<ItemLocation> itemLocations;
    private MinimumAmount minimumAmount;

    private final ItemUtilService itemUtilService = new ItemUtilService();

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

    public Optional<ItemLocation> findItemLocation(UUID itemLocationId) {
        return itemLocations.stream()
                            .filter(i -> i.getId().equals(itemLocationId))
                            .findAny();
    }

    public Optional<ItemLocation> findItemLocation(Location location) {
        return itemLocations.stream()
                            .filter(i -> i.getLocation().equals(location))
                            .findAny();
    }

    public void addItemLocation(ItemLocation itemLocation) {
        itemUtilService.validateUnit(itemReference, itemLocation.getAmount().getUnit().getType());
        if (findItemLocation(itemLocation.getId()).or(() -> findItemLocation(itemLocation.getLocation())).isPresent()) {
            throw new ItemLocationAlreadyExistsException(itemLocation.getId(), id);
        }

        itemLocations.add(itemLocation);
    }

    public void addItemLocation(Location location, Amount amount) {
        addItemLocation(new ItemLocation(location, amount));
    }

    public void updateItemLocationAmount(UUID itemLocationId, Amount amount) {
        itemUtilService.validateUnit(itemReference, amount.getUnit().getType());

        ItemLocation itemLocation = findItemLocation(itemLocationId).orElseThrow(() -> new ItemLocationNotFoundException(itemLocationId, id));
        itemLocation.setAmount(amount);
    }

    public void removeLocation(UUID itemLocationId) {
        findItemLocation(itemLocationId).ifPresent(itemLocations::remove);
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
        itemUtilService.validateUnit(itemReference, minimumAmount.getAmount().getUnit().getType());
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
