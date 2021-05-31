package de.dhbw.application.storage.observer;

import de.dhbw.aggregates.StoredItem;

import java.util.UUID;

public interface ItemLocationObserver {

    void onItemLocationAmountChanged(StoredItem storedItem, UUID itemLocationId);
}
