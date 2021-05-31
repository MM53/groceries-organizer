package de.dhbw.application.storage.observer;

import de.dhbw.aggregates.StoredItem;

import java.util.UUID;

public interface ObservableItemLocation {

    void addObserver(ItemLocationObserver observer);

    void removeObserver(ItemLocationObserver observer);

    void notifyItemLocationAmountChanged(StoredItem storedItem, UUID itemLocationId);
}
