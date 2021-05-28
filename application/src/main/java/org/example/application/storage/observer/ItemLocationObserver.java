package org.example.application.storage.observer;

import org.example.entities.aggregateRoots.StoredItem;

import java.util.UUID;

public interface ItemLocationObserver {

    void onItemLocationAmountChanged(StoredItem storedItem, UUID itemLocationId);
}
