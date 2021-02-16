package org.example.persistence.jooq.dao;

import org.example.aggregates.StoredItem;
import org.example.entities.Item;
import org.example.entities.ItemLocation;
import org.example.entities.MinimumAmount;
import org.example.persistence.jooq.configuration.JooqConnection;
import org.example.persistence.jooq.mapper.ItemLocationMapper;
import org.example.persistence.jooq.mapper.MinimumAmountMapper;
import org.example.repositories.StoredItemRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.example.persistence.jooq.generated.Tables.*;

@Component
public class JooqStoredItemRepository implements StoredItemRepository {

    private final DSLContext context;

    private static final Table<Record> JOINED_TABLE = STORED_ITEM.join(MINIMUM_AMOUNT)
                                                                 .on(STORED_ITEM.MINIMUM_AMOUNT_REFERENCE.eq(MINIMUM_AMOUNT.ID))
                                                                 .join(ITEM_LOCATION)
                                                                 .on(ITEM_LOCATION.STORED_ITEM_REFERENCE.eq(STORED_ITEM.ID));

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
                      .collect(Collectors.groupingBy(record -> record.get(STORED_ITEM.ID)))
                      .values()
                      .stream()
                      .map(JooqStoredItemRepository::mapFromJoinedRecord)
                      .findAny();
    }

    @Override
    public Optional<StoredItem> findByReferencedItem(Item item) {
        return context.fetch(JOINED_TABLE, STORED_ITEM.ITEM_REFERENCE.eq(item.getPrimaryName()))
                      .stream()
                      .collect(Collectors.groupingBy(record -> record.get(STORED_ITEM.ID)))
                      .values()
                      .stream()
                      .map(JooqStoredItemRepository::mapFromJoinedRecord)
                      .findAny();
    }

    @Override
    public List<StoredItem> getAll() {
        return context.fetch(JOINED_TABLE)
                      .stream()
                      .collect(Collectors.groupingBy(record -> record.get(STORED_ITEM.ID)))
                      .values()
                      .stream()
                      .map(JooqStoredItemRepository::mapFromJoinedRecord)
                      .collect(Collectors.toList());
    }

    private static StoredItem mapFromJoinedRecord(List<Record> records) {
        final Record firstRecord = records.get(0);

        MinimumAmount minimumAmount = MinimumAmountMapper.extractRecord(firstRecord)
                                                         .into(MinimumAmount.class);
        Set<ItemLocation> itemLocations = records.stream()
                                                 .map(record -> ItemLocationMapper.extractRecord(record)
                                                                                  .into(ItemLocation.class))
                                                 .collect(Collectors.toSet());

        return new StoredItem(UUID.fromString(firstRecord.get(STORED_ITEM.ID)),
                              firstRecord.get(STORED_ITEM.ITEM_REFERENCE),
                              itemLocations,
                              minimumAmount);
    }
}
