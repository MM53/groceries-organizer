package org.example.adapter.persistence.jooq.mapper.collectors;

import org.example.aggregates.StoredItem;
import org.example.entities.ItemLocation;
import org.example.entities.MinimumAmount;
import org.example.exceptions.ItemNotFoundException;
import org.example.exceptions.UnitMismatchException;
import org.jooq.Record;

import java.util.HashSet;
import java.util.UUID;

import static org.example.adapter.persistence.jooq.generated.Tables.*;

public class StoredItemCollector extends RecordCollector<StoredItem> {

    @Override
    StoredItem newEntityFromRecord(Record record) {
        return new StoredItem(UUID.fromString(record.get(STORED_ITEM.ID)),
                              record.get(STORED_ITEM.ITEM_REFERENCE),
                              new HashSet<>(),
                              record.into(MINIMUM_AMOUNT).into(MinimumAmount.class));
    }

    @Override
    StoredItem updateEntityFromRecord(Record record, StoredItem storedItem) {
        if (record.get(ITEM_LOCATION.ID) != null) {
            try {
                storedItem.addItemLocation(record.into(ITEM_LOCATION).into(ItemLocation.class));
            } catch (UnitMismatchException | ItemNotFoundException e) {
                System.out.println("Error while loading item locations: " + e.getMessage());
            }
        }
        return storedItem;
    }
}
