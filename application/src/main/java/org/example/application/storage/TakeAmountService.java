package org.example.application.storage;

import org.example.entities.aggregateRoots.StoredItem;
import org.example.exceptions.ItemLocationNotFoundException;
import org.example.valueObjects.Amount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TakeAmountService {

    private final ReadStorageService readStorageService;
    private final UpdateStorageService updateStorageService;

    @Autowired
    public TakeAmountService(ReadStorageService readStorageService, UpdateStorageService updateStorageService) {
        this.readStorageService = readStorageService;
        this.updateStorageService = updateStorageService;
    }

    public Amount takeAmount(String itemName, Amount requestedAmount, UUID itemLocationId) {
        StoredItem storedItem = readStorageService.getStoredItem(itemName);
        Amount availableAmount = storedItem.findItemLocation(itemLocationId)
                                           .orElseThrow(() -> new ItemLocationNotFoundException(itemLocationId, storedItem.getId()))
                                           .getAmount();

        if (requestedAmount.isMoreThan(availableAmount) || requestedAmount.equals(availableAmount)) {
            updateStorageService.removeItemLocation(itemName, itemLocationId);
            return requestedAmount.sub(availableAmount);
        } else {
            updateStorageService.updateAmount(itemName, itemLocationId, availableAmount.sub(requestedAmount));
            return new Amount(0, requestedAmount.getUnit());
        }
    }
}
