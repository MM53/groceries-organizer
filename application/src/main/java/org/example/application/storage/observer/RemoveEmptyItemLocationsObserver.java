package org.example.application.storage.observer;

import org.example.application.storage.UpdateStorageService;
import org.example.entities.aggregateRoots.StoredItem;
import org.example.exceptions.ItemLocationNotFoundException;
import org.example.valueObjects.Amount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoveEmptyItemLocationsObserver implements ItemLocationObserver {

    private final UpdateStorageService updateStorageService;

    @Autowired
    public RemoveEmptyItemLocationsObserver(UpdateStorageService updateStorageService) {
        this.updateStorageService = updateStorageService;
    }

    @Override
    public void onItemLocationAmountChanged(StoredItem storedItem, UUID itemLocationId) {
        Amount availableAmount = storedItem.findItemLocation(itemLocationId)
                                           .orElseThrow(() -> new ItemLocationNotFoundException(itemLocationId, storedItem.getId()))
                                           .getAmount();

        if (availableAmount.isEmpty()) {
            updateStorageService.removeItemLocation(storedItem.getItemReference(), itemLocationId);
        }
    }
}
