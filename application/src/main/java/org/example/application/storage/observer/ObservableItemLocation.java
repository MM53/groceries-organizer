package org.example.application.storage.observer;

import org.example.entities.aggregateRoots.StoredItem;

import java.util.UUID;

public interface ObservableItemLocation {

    void addObserver(ItemLocationObserver observer);

    void removeObserver(ItemLocationObserver observer);

    void notifyItemLocationAmountChanged(StoredItem storedItem, UUID itemLocationId);
}
