package org.example.persistence.jooq.dao;

import org.example.entities.Item;
import org.example.entities.ItemNameAlternative;
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
import static org.example.persistence.jooq.generated.Tables.ITEM_ALTERNATIVE_NAME;

@Component
public class JooqItemRepository implements ItemRepository {

    private final DSLContext context;

    private static final Table<Record> JOINED_TABLE = ITEM.leftJoin(ITEM_ALTERNATIVE_NAME)
                                                          .on(ITEM_ALTERNATIVE_NAME.ALTERNATIVE_FOR.eq(ITEM.PRIMARY_NAME));

    @Autowired
    public JooqItemRepository(JooqConnection connection) {
        this.context = connection.getContext();
    }

    @Override
    public void save(Item item) {
        context.newRecord(ITEM, item).merge();
    }

    @Override
    public Optional<Item> findItemByPrimaryName(String name) {
        return context.fetch(JOINED_TABLE, ITEM.PRIMARY_NAME.eq(name))
                      .stream()
                      .collect(Collectors.groupingBy(record -> record.getValue(ITEM.PRIMARY_NAME)))
                      .values()
                      .stream()
                      .map(JooqItemRepository::mapFromJoinedRecord)
                      .findAny();
    }

    @Override
    public List<Item> getAll() {
        return context.fetch(JOINED_TABLE)
                      .stream()
                      .collect(Collectors.groupingBy(record -> record.getValue(ITEM.PRIMARY_NAME)))
                      .values()
                      .stream()
                      .map(JooqItemRepository::mapFromJoinedRecord)
                      .collect(Collectors.toList());
    }

    private static Item mapFromJoinedRecord(List<Record> records) {
        return new Item(records.get(0).getValue(ITEM.PRIMARY_NAME),
                        records.stream()
                               .filter(record -> record.getValue(ITEM_ALTERNATIVE_NAME.NAME) != null)
                               .map(record -> record.into(ITEM_ALTERNATIVE_NAME).into(ItemNameAlternative.class)).collect(Collectors.toSet()),
                        UnitTypes.valueOf(records.get(0).getValue(ITEM.UNIT_TYPE)));
    }
}
