package org.example.application.storage;

import org.example.aggregates.StoredItem;
import org.example.application.storage.observer.CheckMinimumAmountObserver;
import org.example.application.storage.observer.ItemLocationObserver;
import org.example.application.storage.observer.ObservableItemLocation;
import org.example.application.storage.observer.RemoveEmptyItemLocationsObserver;
import org.example.exceptions.ItemLocationNotFoundException;
import org.example.valueObjects.Amount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class TakeAmountService implements ObservableItemLocation {

    private final ReadStorageService readStorageService;
    private final UpdateStorageService updateStorageService;

    private final Set<ItemLocationObserver> observers = new HashSet<>();

    @Autowired
        public TakeAmountService(ReadStorageService readStorageService,
                             UpdateStorageService updateStorageService,
                             RemoveEmptyItemLocationsObserver removeEmptyItemLocationsObserver,
                             CheckMinimumAmountObserver checkMinimumAmountObserver) {
        this.readStorageService = readStorageService;
        this.updateStorageService = updateStorageService;
        addObserver(removeEmptyItemLocationsObserver);
        addObserver(checkMinimumAmountObserver);
    }

    public Amount takeAmount(String itemName, Amount requestedAmount, UUID itemLocationId) {
        StoredItem storedItem = readStorageService.getStoredItem(itemName);
        Amount availableAmount = storedItem.findItemLocation(itemLocationId)
                                           .orElseThrow(() -> new ItemLocationNotFoundException(itemLocationId, storedItem.getId()))
                                           .getAmount();
        Amount requiredAmountLeft;
        if (requestedAmount.isMoreThan(availableAmount) || requestedAmount.equals(availableAmount)) {
            updateStorageService.updateAmount(itemName, itemLocationId, new Amount(0, availableAmount.getUnit()));
            requiredAmountLeft = requestedAmount.sub(availableAmount);
        } else {
            updateStorageService.updateAmount(itemName, itemLocationId, availableAmount.sub(requestedAmount));
            requiredAmountLeft = new Amount(0, requestedAmount.getUnit());
        }
        notifyItemLocationAmountChanged(readStorageService.getStoredItem(itemName), itemLocationId);
        return requiredAmountLeft;
    }

    @Override
    public void addObserver(ItemLocationObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(ItemLocationObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyItemLocationAmountChanged(StoredItem storedItem, UUID itemLocationId) {
        observers.forEach(observer -> observer.onItemLocationAmountChanged(storedItem, itemLocationId));
    }
}
