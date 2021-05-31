package de.dhbw.adapter.persistence.jooq.mapper.collectors;

import de.dhbw.adapter.persistence.jooq.generated.Tables;
import de.dhbw.aggregates.StoredItem;
import de.dhbw.entities.ItemLocation;
import de.dhbw.entities.MinimumAmount;
import de.dhbw.exceptions.ItemNotFoundException;
import de.dhbw.exceptions.UnitMismatchException;
import org.jooq.Record;

import java.util.HashSet;
import java.util.UUID;

public class StoredItemCollector extends RecordCollector<StoredItem> {

    @Override
    StoredItem newEntityFromRecord(Record record) {
        return new StoredItem(UUID.fromString(record.get(Tables.STORED_ITEM.ID)),
                              record.get(Tables.STORED_ITEM.ITEM_REFERENCE),
                              new HashSet<>(),
                              record.into(Tables.MINIMUM_AMOUNT).into(MinimumAmount.class));
    }

    @Override
    StoredItem updateEntityFromRecord(Record record, StoredItem storedItem) {
        if (record.get(Tables.ITEM_LOCATION.ID) != null) {
            try {
                storedItem.addItemLocation(record.into(Tables.ITEM_LOCATION).into(ItemLocation.class));
            } catch (UnitMismatchException | ItemNotFoundException e) {
                System.out.println("Error while loading item locations: " + e.getMessage());
            }
        }
        return storedItem;
    }
}
