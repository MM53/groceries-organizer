package de.dhbw.plugins.persistence.jooq.repositories;

import de.dhbw.aggregates.ShoppingList;
import de.dhbw.entities.ShoppingListItem;
import de.dhbw.plugins.persistence.jooq.configuration.JooqConnection;
import de.dhbw.plugins.persistence.jooq.generated.Tables;
import de.dhbw.plugins.persistence.jooq.mapper.collectors.ShoppingListCollector;
import de.dhbw.plugins.persistence.jooq.mapper.records.ShoppingListItemMapper;
import de.dhbw.repositories.ShoppingListRepository;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JooqShoppingListRepository implements ShoppingListRepository {

    private final DSLContext context;
    private final ShoppingListCollector collector = new ShoppingListCollector();

    private static final Table<Record> JOINED_TABLE = Tables.SHOPPING_LIST.leftJoin(Tables.SHOPPING_LIST_ITEM)
                                                                          .on(Tables.SHOPPING_LIST_ITEM.SHOPPING_LIST_REFERENCE.eq(Tables.SHOPPING_LIST.NAME));


    @Autowired
    public JooqShoppingListRepository(JooqConnection connection) {
        this.context = connection.getContext();
    }

    @Override
    public void save(ShoppingList shoppingList) {
        context.newRecord(Tables.SHOPPING_LIST, shoppingList).merge();

        saveShoppingListItems(shoppingList);
        deleteRemovedShoppingListItems(shoppingList);
    }

    private void saveShoppingListItems(ShoppingList shoppingList) {
        ShoppingListItemMapper.extractRecords(shoppingList)
                              .forEach(record -> context.newRecord(Tables.SHOPPING_LIST_ITEM, record).merge());
    }

    private void deleteRemovedShoppingListItems(ShoppingList shoppingList) {
        Condition shoppingListItemsRemoved = Tables.SHOPPING_LIST_ITEM.SHOPPING_LIST_REFERENCE.eq(shoppingList.getName());
        if (shoppingList.getShoppingListItems().size() > 0) {
            shoppingListItemsRemoved = shoppingListItemsRemoved.and(Tables.SHOPPING_LIST_ITEM.ID.notIn(shoppingList.getShoppingListItems()
                                                                                                                   .stream()
                                                                                                                   .map(ShoppingListItem::getId)
                                                                                                                   .toList()));
        }
        context.delete(Tables.SHOPPING_LIST_ITEM).where(shoppingListItemsRemoved).execute();
    }

    @Override
    public Optional<ShoppingList> findByListName(String listName) {
        return context.fetch(JOINED_TABLE, Tables.SHOPPING_LIST.NAME.eq(listName))
                      .stream()
                      .collect(collector.toOptional());
    }

    @Override
    public boolean checkExistenceByListName(String listName) {
        return context.fetchExists(Tables.SHOPPING_LIST, Tables.SHOPPING_LIST.NAME.eq(listName));
    }

    @Override
    public List<ShoppingList> getAll() {
        return context.fetch(JOINED_TABLE)
                      .stream()
                      .collect(collector.toList(Tables.SHOPPING_LIST.NAME));
    }

    @Override
    public void delete(ShoppingList shoppingList) {
        context.newRecord(Tables.SHOPPING_LIST, shoppingList).delete();
    }
}
