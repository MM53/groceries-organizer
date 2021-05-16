package org.example.application.storage;

import org.example.application.items.ManageItemsService;
import org.example.entities.aggregateRoots.Item;
import org.example.entities.aggregateRoots.StoredItem;
import org.example.exceptions.ItemLocationNotFoundException;
import org.example.repositories.StoredItemRepository;
import org.example.valueObjects.Amount;
import org.example.valueObjects.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateStorageService {

    private final StoredItemRepository storedItemRepository;
    private final ManageItemsService manageItemsService;
    private final ReadStorageService readStorageService;

    @Autowired
    public UpdateStorageService(StoredItemRepository storedItemRepository, ManageItemsService manageItemsService, ReadStorageService readStorageService) {
        this.storedItemRepository = storedItemRepository;
        this.manageItemsService = manageItemsService;
        this.readStorageService = readStorageService;
    }

    public void createAndStoreItem(String itemName, Location location, Amount amount) {
        manageItemsService.createItem(itemName, amount.getUnit().getType());
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

    public void deleteStoredItem(String itemName) {
        storedItemRepository.delete(readStorageService.getStoredItem(itemName));
    }

    public Amount takeAmount(String itemName, Amount requestedAmount, UUID itemLocationId) {
        StoredItem storedItem = readStorageService.getStoredItem(itemName);
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