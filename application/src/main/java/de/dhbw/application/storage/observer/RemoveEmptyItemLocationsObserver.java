package de.dhbw.application.storage.observer;

import de.dhbw.aggregates.StoredItem;
import de.dhbw.application.storage.ManageStorageService;
import de.dhbw.exceptions.ItemLocationNotFoundException;
import de.dhbw.valueObjects.Amount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoveEmptyItemLocationsObserver implements ItemLocationObserver {

    private final ManageStorageService manageStorageService;

    @Autowired
    public RemoveEmptyItemLocationsObserver(ManageStorageService manageStorageService) {
        this.manageStorageService = manageStorageService;
    }

    @Override
    public void onItemLocationAmountChanged(StoredItem storedItem, UUID itemLocationId) {
        Amount availableAmount = storedItem.findItemLocation(itemLocationId)
                                           .orElseThrow(() -> new ItemLocationNotFoundException(itemLocationId, storedItem.getId()))
                                           .getAmount();

        if (availableAmount.isEmpty()) {
            manageStorageService.removeItemLocation(storedItem.getItemReference(), itemLocationId);
        }
    }
}
