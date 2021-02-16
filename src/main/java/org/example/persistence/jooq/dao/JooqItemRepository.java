package org.example.persistence.jooq.dao;

import org.example.entities.ItemName;
import org.example.entities.aggregateRoots.Item;
import org.example.persistence.jooq.configuration.JooqConnection;
import org.example.repositories.ItemRepository;
import org.example.units.UnitTypes;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.example.persistence.jooq.generated.Tables.ITEM;
import static org.example.persistence.jooq.generated.Tables.ITEM_NAME;

@Component
public class JooqItemRepository implements ItemRepository {

    private final DSLContext context;

    private static final Table<Record> JOINED_TABLE = ITEM.leftJoin(ITEM_NAME)
                                                          .on(ITEM_NAME.ITEM_REFERENCE.eq(ITEM.ID));

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
                      .collect(Collectors.groupingBy(record -> record.getValue(ITEM.ID)))
                      .values()
                      .stream()
                      .map(JooqItemRepository::mapFromJoinedRecord)
                      .findAny();
    }

    @Override
    public Optional<Item> findItemForName(String name) {
        return context.fetch(JOINED_TABLE, ITEM_NAME.NAME.eq(name))
                      .stream()
                      .collect(Collectors.groupingBy(record -> record.getValue(ITEM.ID)))
                      .values()
                      .stream()
                      .map(JooqItemRepository::mapFromJoinedRecord)
                      .findAny();
    }

    @Override
    public List<Item> getAll() {
        return context.fetch(JOINED_TABLE)
                      .stream()
                      .collect(Collectors.groupingBy(record -> record.getValue(ITEM.ID)))
                      .values()
                      .stream()
                      .map(JooqItemRepository::mapFromJoinedRecord)
                      .collect(Collectors.toList());
    }

    private static Item mapFromJoinedRecord(List<Record> records) {
        return new Item(records.get(0).getValue(ITEM.ID),
                        records.stream()
                               .filter(record -> record.getValue(ITEM_NAME.NAME) != null)
                               .map(record -> record.into(ITEM_NAME).into(ItemName.class)).collect(Collectors.toSet()),
                        UnitTypes.valueOf(records.get(0).getValue(ITEM.UNIT_TYPE)));
    }
}
