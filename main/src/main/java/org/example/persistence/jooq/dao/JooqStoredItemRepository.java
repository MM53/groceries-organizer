package org.example.persistence.jooq.dao;

import org.example.entities.ItemLocation;
import org.example.entities.aggregateRoots.Item;
import org.example.entities.aggregateRoots.StoredItem;
import org.example.persistence.jooq.configuration.JooqConnection;
import org.example.persistence.jooq.generated.tables.records.StoredItemRecord;
import org.example.persistence.jooq.mapper.collectors.StoredItemCollector;
import org.example.persistence.jooq.mapper.records.ItemLocationMapper;
import org.example.persistence.jooq.mapper.records.MinimumAmountMapper;
import org.example.repositories.StoredItemRepository;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.example.persistence.jooq.generated.Tables.*;

@Component
public class JooqStoredItemRepository implements StoredItemRepository {

    private final DSLContext context;
    private final StoredItemCollector collector = new StoredItemCollector();

    private static final Table<Record> JOINED_TABLE = STORED_ITEM.leftJoin(MINIMUM_AMOUNT)
                                                                 .on(STORED_ITEM.MINIMUM_AMOUNT_REFERENCE.eq(MINIMUM_AMOUNT.ID))
                                                                 .leftJoin(ITEM_LOCATION)
                                                                 .on(ITEM_LOCATION.STORED_ITEM_REFERENCE.eq(STORED_ITEM.ID));

    @Autowired
    public JooqStoredItemRepository(JooqConnection connection) {
        this.context = connection.getContext();
    }

    @Override
    public void save(StoredItem storedItem) {
        StoredItemRecord storedItemRecord = context.newRecord(STORED_ITEM);
        storedItemRecord.setId(storedItem.getId().toString());
        storedItemRecord.setItemReference(storedItem.getItemReference());

        if (storedItem.getMinimumAmount() != null) {
            context.newRecord(MINIMUM_AMOUNT, MinimumAmountMapper.unmap(storedItem.getMinimumAmount())).merge();
            storedItemRecord.setMinimumAmountReference(storedItem.getMinimumAmount().getId().toString());
        }

        storedItemRecord.merge();

        storedItem.getItemLocations()
                  .forEach(itemLocation -> context.newRecord(ITEM_LOCATION, ItemLocationMapper.unmap(itemLocation)).merge());

        Condition itemLocationsRemoved = ITEM_LOCATION.STORED_ITEM_REFERENCE.eq(storedItem.getId().toString());
        if (storedItem.getItemLocations().size() > 0) {
            itemLocationsRemoved = itemLocationsRemoved.and(ITEM_LOCATION.ID.notIn(storedItem.getItemLocations()
                                                                                             .stream()
                                                                                             .map(ItemLocation::getId)
                                                                                             .collect(Collectors.toList())));
        }
        context.delete(ITEM_LOCATION).where(itemLocationsRemoved).execute();
    }

    @Override
    public Optional<StoredItem> findById(UUID id) {

        return context.fetch(JOINED_TABLE, STORED_ITEM.ID.eq(id.toString()))
                      .stream()
                      .collect(collector.toOptional());
    }

    @Override
    public Optional<StoredItem> findByReferencedItem(Item item) {
        return context.fetch(JOINED_TABLE, STORED_ITEM.ITEM_REFERENCE.eq(item.getId()))
                      .stream()
                      .collect(collector.toOptional());
    }

    @Override
    public List<StoredItem> getAll() {
        return context.fetch(JOINED_TABLE)
                      .stream()
                      .collect(collector.toList(STORED_ITEM.ID));
    }

    @Override
    public void delete(StoredItem storedItem) {
        context.newRecord(STORED_ITEM, storedItem).delete();
    }
}
