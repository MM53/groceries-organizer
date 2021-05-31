package de.dhbw.plugins.persistence.jooq.repositories;

import de.dhbw.adapter.persistence.jooq.generated.Tables;
import de.dhbw.adapter.persistence.jooq.mapper.collectors.ItemCollector;
import de.dhbw.aggregates.Item;
import de.dhbw.entities.ItemName;
import de.dhbw.plugins.persistence.jooq.configuration.JooqConnection;
import de.dhbw.repositories.ItemRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JooqItemRepository implements ItemRepository {

    private final DSLContext context;
    private final ItemCollector collector = new ItemCollector();

    private static final Table<Record> JOINED_TABLE = Tables.ITEM.leftJoin(Tables.ITEM_NAME)
                                                                 .on(Tables.ITEM_NAME.ITEM_REFERENCE.eq(Tables.ITEM.ID));


    @Autowired
    public JooqItemRepository(JooqConnection connection) {
        this.context = connection.getContext();
    }

    @Override
    public void save(Item item) {
        context.newRecord(Tables.ITEM, item).merge();

        saveItemNames(item);
        deleteRemovedItemNames(item);
    }

    private void saveItemNames(Item item) {
        item.getNames()
            .forEach(itemName -> context.newRecord(Tables.ITEM_NAME, itemName).merge());
    }

    private void deleteRemovedItemNames(Item item) {
        context.delete(Tables.ITEM_NAME)
               .where(Tables.ITEM_NAME.ITEM_REFERENCE.eq(item.getId())
                                                     .and(Tables.ITEM_NAME.NAME.notIn(item.getNames()
                                                                                          .stream()
                                                                                          .map(ItemName::getName)
                                                                                          .toList())))
               .execute();
    }

    @Override
    public Optional<Item> findItemById(String id) {
        return context.fetch(JOINED_TABLE, Tables.ITEM.ID.eq(id))
                      .stream()
                      .collect(collector.toOptional());
    }

    @Override
    public boolean checkExistenceById(String id) {
        return context.fetchExists(JOINED_TABLE, Tables.ITEM.ID.eq(id));
    }

    @Override
    public Optional<Item> findItemByName(String name) {
        return context.fetchOptional(Tables.ITEM_NAME, Tables.ITEM_NAME.NAME.eq(name))
                      .flatMap(record -> findItemById(record.getItemReference()));
    }

    @Override
    public List<Item> getAll() {
        return context.fetch(JOINED_TABLE)
                      .stream()
                      .collect(collector.toList(Tables.ITEM.ID));
    }

    @Override
    public void delete(Item item) {
        context.newRecord(Tables.ITEM, item).delete();
    }
}
