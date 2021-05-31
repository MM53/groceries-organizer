package de.dhbw.plugins.persistence.jooq.repositories;

import de.dhbw.adapter.persistence.jooq.generated.Tables;
import de.dhbw.adapter.persistence.jooq.generated.tables.records.StoredItemRecord;
import de.dhbw.adapter.persistence.jooq.mapper.collectors.StoredItemCollector;
import de.dhbw.adapter.persistence.jooq.mapper.records.ItemLocationMapper;
import de.dhbw.adapter.persistence.jooq.mapper.records.MinimumAmountMapper;
import de.dhbw.aggregates.Item;
import de.dhbw.aggregates.StoredItem;
import de.dhbw.entities.ItemLocation;
import de.dhbw.plugins.persistence.jooq.configuration.JooqConnection;
import de.dhbw.repositories.StoredItemRepository;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JooqStoredItemRepository implements StoredItemRepository {

    private final DSLContext context;
    private final StoredItemCollector collector = new StoredItemCollector();

    private static final Table<Record> JOINED_TABLE = Tables.STORED_ITEM.leftJoin(Tables.MINIMUM_AMOUNT)
                                                                        .on(Tables.STORED_ITEM.MINIMUM_AMOUNT_REFERENCE.eq(Tables.MINIMUM_AMOUNT.ID))
                                                                        .leftJoin(Tables.ITEM_LOCATION)
                                                                        .on(Tables.ITEM_LOCATION.STORED_ITEM_REFERENCE.eq(Tables.STORED_ITEM.ID));

    @Autowired
    public JooqStoredItemRepository(JooqConnection connection) {
        this.context = connection.getContext();
    }

    @Override
    public void save(StoredItem storedItem) {
        saveMinimumAmount(storedItem);

        saveStoredItem(storedItem);

        saveItemLocations(storedItem);
        deleteRemovedItemLocations(storedItem);

    }

    private void saveStoredItem(StoredItem storedItem) {
        StoredItemRecord storedItemRecord = context.newRecord(Tables.STORED_ITEM);
        storedItemRecord.setId(storedItem.getId().toString());
        storedItemRecord.setItemReference(storedItem.getItemReference());
        if (storedItem.getMinimumAmount() != null) {
            storedItemRecord.setMinimumAmountReference(storedItem.getMinimumAmount().getId().toString());
        }
        storedItemRecord.merge();
    }

    private void saveMinimumAmount(StoredItem storedItem) {
        if (storedItem.getMinimumAmount() != null) {
            context.newRecord(Tables.MINIMUM_AMOUNT, MinimumAmountMapper.extractRecord(storedItem)).merge();
        }
    }

    private void saveItemLocations(StoredItem storedItem) {
        ItemLocationMapper.extractRecords(storedItem)
                          .forEach(record -> context.newRecord(Tables.ITEM_LOCATION, record).merge());
    }

    private void deleteRemovedItemLocations(StoredItem storedItem) {
        Condition itemLocationsRemoved = Tables.ITEM_LOCATION.STORED_ITEM_REFERENCE.eq(storedItem.getId().toString());
        if (storedItem.getItemLocations().size() > 0) {
            itemLocationsRemoved = itemLocationsRemoved.and(Tables.ITEM_LOCATION.ID.notIn(storedItem.getItemLocations()
                                                                                                    .stream()
                                                                                                    .map(ItemLocation::getId)
                                                                                                    .toList()));
        }
        context.delete(Tables.ITEM_LOCATION).where(itemLocationsRemoved).execute();
    }

    @Override
    public Optional<StoredItem> findById(UUID id) {

        return context.fetch(JOINED_TABLE, Tables.STORED_ITEM.ID.eq(id.toString()))
                      .stream()
                      .collect(collector.toOptional());
    }

    @Override
    public Optional<StoredItem> findByReferencedItem(Item item) {
        return context.fetch(JOINED_TABLE, Tables.STORED_ITEM.ITEM_REFERENCE.eq(item.getId()))
                      .stream()
                      .collect(collector.toOptional());
    }

    @Override
    public List<StoredItem> getAll() {
        return context.fetch(JOINED_TABLE)
                      .stream()
                      .collect(collector.toList(Tables.STORED_ITEM.ID));
    }

    @Override
    public void delete(StoredItem storedItem) {
        context.newRecord(Tables.STORED_ITEM, storedItem).delete();
    }
}
