package de.dhbw.application.storage.observer;

import de.dhbw.aggregates.StoredItem;
import de.dhbw.application.storage.UpdateStorageService;
import de.dhbw.exceptions.ItemLocationNotFoundException;
import de.dhbw.valueObjects.Amount;
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
