package org.example.persistence.jooq.dao;

import org.example.entities.ItemLocation;
import org.example.entities.MinimumAmount;
import org.example.entities.aggregateRoots.Item;
import org.example.entities.aggregateRoots.StoredItem;
import org.example.exceptions.ItemNotFoundException;
import org.example.exceptions.UnitMismatchException;
import org.example.persistence.jooq.configuration.JooqConnection;
import org.example.persistence.jooq.mapper.collectors.ListRecordCollector;
import org.example.persistence.jooq.mapper.collectors.OptionalRecordCollector;
import org.example.persistence.jooq.mapper.records.ItemLocationMapper;
import org.example.persistence.jooq.mapper.records.MinimumAmountMapper;
import org.example.repositories.StoredItemRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.example.persistence.jooq.generated.Tables.*;

@Component
public class JooqStoredItemRepository implements StoredItemRepository {

    private final DSLContext context;

    private static final Table<Record> JOINED_TABLE = STORED_ITEM.join(MINIMUM_AMOUNT)
                                                                 .on(STORED_ITEM.MINIMUM_AMOUNT_REFERENCE.eq(MINIMUM_AMOUNT.ID))
                                                                 .join(ITEM_LOCATION)
                                                                 .on(ITEM_LOCATION.STORED_ITEM_REFERENCE.eq(STORED_ITEM.ID));
    private static final ListRecordCollector<StoredItem> LIST_RECORD_COLLECTOR = new ListRecordCollector<>(STORED_ITEM.ID,
                                                                                                           JooqStoredItemRepository::storedItemFromRecord,
                                                                                                           JooqStoredItemRepository::updateStoredItemFromRecord);
    private static final OptionalRecordCollector<StoredItem> OPTIONAL_RECORD_COLLECTOR = new OptionalRecordCollector<>(JooqStoredItemRepository::storedItemFromRecord,
                                                                                                                       JooqStoredItemRepository::updateStoredItemFromRecord);

    @Autowired
    public JooqStoredItemRepository(JooqConnection connection) {
        this.context = connection.getContext();
    }

    @Override
    public void save(StoredItem storedItem) {
        context.newRecord(MINIMUM_AMOUNT, MinimumAmountMapper.unmap(storedItem.getMinimumAmount())).merge();

        context.newRecord(STORED_ITEM)
               .values(storedItem.getId().toString(),
                       storedItem.getItemReference(),
                       storedItem.getMinimumAmount().getId().toString())
               .merge();

        storedItem.getItemLocations()
                  .forEach(itemLocation -> context.newRecord(ITEM_LOCATION, ItemLocationMapper.unmap(itemLocation))
                                                  .merge());
    }

    @Override
    public Optional<StoredItem> findById(UUID id) {

        return context.fetch(JOINED_TABLE, STORED_ITEM.ID.eq(id.toString()))
                      .stream()
                      .collect(OPTIONAL_RECORD_COLLECTOR);
    }

    @Override
    public Optional<StoredItem> findByReferencedItem(Item item) {
        return context.fetch(JOINED_TABLE, STORED_ITEM.ITEM_REFERENCE.eq(item.getId()))
                      .stream()
                      .collect(OPTIONAL_RECORD_COLLECTOR);
    }

    @Override
    public List<StoredItem> getAll() {
        return context.fetch(JOINED_TABLE)
                      .stream()
                      .collect(LIST_RECORD_COLLECTOR);
    }

    private static StoredItem storedItemFromRecord(Record record) {
        return new StoredItem(UUID.fromString(record.get(STORED_ITEM.ID)),
                              record.get(STORED_ITEM.ITEM_REFERENCE),
                              new HashSet<>(),
                              record.into(MINIMUM_AMOUNT).into(MinimumAmount.class));
    }

    private static StoredItem updateStoredItemFromRecord(Record record, StoredItem storedItem) {
        try {
            storedItem.addItemLocation(record.into(ITEM_LOCATION).into(ItemLocation.class));
        } catch (UnitMismatchException | ItemNotFoundException e) {
            System.out.println("Error while loading item locations: " + e.getMessage());
        }
        return storedItem;
    }
}
