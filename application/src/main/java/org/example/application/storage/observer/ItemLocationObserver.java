package org.example.application.storage.observer;

import org.example.aggregates.StoredItem;

import java.util.UUID;

public interface ItemLocationObserver {

    void onItemLocationAmountChanged(StoredItem storedItem, UUID itemLocationId);
}
