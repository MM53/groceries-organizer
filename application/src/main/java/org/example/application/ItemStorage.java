package org.example.application;

import org.example.entities.ItemLocation;
import org.example.entities.aggregateRoots.Item;
import org.example.entities.aggregateRoots.StoredItem;
import org.example.application.exceptions.StoredItemNotFoundException;
import org.example.exceptions.ItemLocationNotFoundException;
import org.example.repositories.StoredItemRepository;
import org.example.valueObjects.Amount;
import org.example.valueObjects.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ItemStorage {

    private final StoredItemRepository storedItemRepository;
    private final ItemManager itemManager;

    @Autowired
    public ItemStorage(StoredItemRepository storedItemRepository, ItemManager itemManager) {
        this.storedItemRepository = storedItemRepository;
        this.itemManager = itemManager;
    }

    public void createAndStoreItem(String itemName, Location location, Amount amount) {
        itemManager.createItem(itemName, amount.getUnit().getType());
        storeItem(itemName, location, amount);
    }

    public void storeItem(String itemName, Location location, Amount amount) {
        StoredItem storedItem = storedItemRepository.findByReferencedItem(new Item(itemName, null))
                                                    .orElse(new StoredItem(itemName, null));
        storedItem.findItemLocation(location)
                  .ifPresentOrElse(itemLocation -> storedItem.updateItemLocationAmount(itemLocation.getId(), amount),
                                   () -> storedItem.addItemLocation(location, amount));
        storedItemRepository.save(storedItem);
    }

    public void setMinimumAmount(String itemName, Amount amount) {
        StoredItem storedItem = storedItemRepository.findByReferencedItem(new Item(itemName, null))
                                                    .map(item -> {
                                                        item.setMinimumAmount(amount);
                                                        return item;
                                                    })
                                                    .orElse(new StoredItem(itemName, amount));
        storedItemRepository.save(storedItem);
    }

    public List<StoredItem> listStoredItems() {
        return storedItemRepository.getAll();
    }

    public StoredItem viewStoredItem(String itemName) {
        return storedItemRepository.findByReferencedItem(new Item(itemName, null))
                                   .orElseThrow(() -> new StoredItemNotFoundException(itemName));
    }

    public List<ItemLocation> listItemLocations(String itemName) {
        return new ArrayList<>(storedItemRepository.findByReferencedItem(new Item(itemName, null))
                                                   .map(StoredItem::getItemLocations)
                                                   .orElseThrow(() -> new StoredItemNotFoundException(itemName)));
    }

    public ItemLocation getItemLocation(String itemName, Location location) {
//        TODO
        return storedItemRepository.findByReferencedItem(new Item(itemName, null))
                                   .map(StoredItem::getItemLocations)
                                   .orElseThrow(() -> new StoredItemNotFoundException(itemName))
                                   .stream()
                                   .filter(itemLocation -> itemLocation.getLocation().equals(location))
                                   .findAny()
                                   .get();
    }

    public void deleteStoredItem(String itemName) {
        StoredItem storedItem = storedItemRepository.findByReferencedItem(new Item(itemName, null))
                                                    .orElseThrow(() -> new StoredItemNotFoundException(itemName));
        storedItemRepository.delete(storedItem);
    }

    public Amount takeAmount(String itemName, Amount requestedAmount, UUID itemLocationId) {
        StoredItem storedItem = storedItemRepository.findByReferencedItem(new Item(itemName, null))
                                                    .orElseThrow(() -> new StoredItemNotFoundException(itemName));

        Amount availableAmount = storedItem.findItemLocation(itemLocationId)
                                           .orElseThrow(() -> new ItemLocationNotFoundException(itemLocationId, storedItem.getId()))
                                           .getAmount();

        if (requestedAmount.isMoreThan(availableAmount) || requestedAmount.equals(availableAmount)) {
            storedItem.removeLocation(itemLocationId);
            return requestedAmount.sub(availableAmount);
        } else {
            storedItem.updateItemLocationAmount(itemLocationId, availableAmount.sub(requestedAmount));
            return new Amount(0, requestedAmount.getUnit());
        }
    }
}
