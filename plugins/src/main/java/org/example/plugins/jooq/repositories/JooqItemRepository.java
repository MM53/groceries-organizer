package org.example.plugins.jooq.repositories;

import org.example.entities.ItemName;
import org.example.entities.aggregateRoots.Item;
import org.example.plugins.jooq.configuration.JooqConnection;
import org.example.adapter.persistence.jooq.mapper.collectors.ItemCollector;
import org.example.repositories.ItemRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.example.adapter.persistence.jooq.generated.Tables.ITEM;
import static org.example.adapter.persistence.jooq.generated.Tables.ITEM_NAME;

@Component
public class JooqItemRepository implements ItemRepository {

    private final DSLContext context;
    private final ItemCollector collector = new ItemCollector();

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
        context.delete(ITEM_NAME)
               .where(ITEM_NAME.ITEM_REFERENCE.eq(item.getId())
                                              .and(ITEM_NAME.NAME.notIn(item.getNames()
                                                                            .stream()
                                                                            .map(ItemName::getName)
                                                                            .collect(Collectors.toList()))))
               .execute();
    }

    @Override
    public Optional<Item> findItemById(String id) {
        return context.fetch(JOINED_TABLE, ITEM.ID.eq(id))
                      .stream()
                      .collect(collector.toOptional());
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
                      .collect(collector.toList(ITEM.ID));
    }

    @Override
    public void delete(Item item) {
        context.newRecord(ITEM, item).delete();
    }
}
