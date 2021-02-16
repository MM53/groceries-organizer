package org.example.persistence.jooq.dao;

import org.example.entities.ItemName;
import org.example.entities.aggregateRoots.Item;
import org.example.persistence.jooq.configuration.JooqConnection;
import org.example.persistence.jooq.mapper.collectors.ListRecordCollector;
import org.example.persistence.jooq.mapper.collectors.OptionalRecordCollector;
import org.example.repositories.ItemRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.example.persistence.jooq.generated.Tables.ITEM;
import static org.example.persistence.jooq.generated.Tables.ITEM_NAME;

@Component
public class JooqItemRepository implements ItemRepository {

    private final DSLContext context;

    private static final Table<Record> JOINED_TABLE = ITEM.leftJoin(ITEM_NAME)
                                                          .on(ITEM_NAME.ITEM_REFERENCE.eq(ITEM.ID));
    private static final ListRecordCollector<Item> LIST_RECORD_COLLECTOR = new ListRecordCollector<>(ITEM.ID,
                                                                                                     JooqItemRepository::itemFromRecord,
                                                                                                     JooqItemRepository::updateItemFromRecord);
    private static final OptionalRecordCollector<Item> OPTIONAL_RECORD_COLLECTOR = new OptionalRecordCollector<>(JooqItemRepository::itemFromRecord,
                                                                                                                 JooqItemRepository::updateItemFromRecord);


    @Autowired
    public JooqItemRepository(JooqConnection connection) {
        this.context = connection.getContext();
    }

    @Override
    public void save(Item item) {
        context.newRecord(ITEM, item).merge();
        item.getNames().forEach(itemName -> context.newRecord(ITEM_NAME, itemName).merge());
    }

    @Override
    public Optional<Item> findItemById(String id) {
        return context.fetch(JOINED_TABLE, ITEM.ID.eq(id))
                      .stream()
                      .collect(OPTIONAL_RECORD_COLLECTOR);
    }

    @Override
    public Optional<Item> findItemByName(String name) {
        return context.fetchOptional(ITEM_NAME, ITEM_NAME.NAME.eq(name))
                      .flatMap(record -> findItemById(record.getItemReference()));
    }

    @Override
    public List<Item> getAll() {
        return context.fetch(JOINED_TABLE)
                      .stream()
                      .collect(LIST_RECORD_COLLECTOR);
    }

    private static Item itemFromRecord(Record record) {
        return record.into(ITEM).into(Item.class);
    }

    private static Item updateItemFromRecord(Record record, Item item) {
        item.addAlternativeName(record.into(ITEM_NAME).into(ItemName.class));
        return item;
    }
}
