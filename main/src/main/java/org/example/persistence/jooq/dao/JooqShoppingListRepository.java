package org.example.persistence.jooq.dao;

import org.example.entities.ShoppingListItem;
import org.example.entities.aggregateRoots.ShoppingList;
import org.example.persistence.jooq.configuration.JooqConnection;
import org.example.persistence.jooq.mapper.collectors.ShoppingListCollector;
import org.example.persistence.jooq.mapper.records.ShoppingListItemMapper;
import org.example.repositories.ShoppingListRepository;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.example.persistence.jooq.generated.Tables.SHOPPING_LIST;
import static org.example.persistence.jooq.generated.Tables.SHOPPING_LIST_ITEM;

@Component
public class JooqShoppingListRepository implements ShoppingListRepository {

    private final DSLContext context;
    private final ShoppingListCollector collector = new ShoppingListCollector();

    private static final Table<Record> JOINED_TABLE = SHOPPING_LIST.leftJoin(SHOPPING_LIST_ITEM)
                                                                   .on(SHOPPING_LIST_ITEM.SHOPPING_LIST_REFERENCE.eq(SHOPPING_LIST.NAME));


    @Autowired
    public JooqShoppingListRepository(JooqConnection connection) {
        this.context = connection.getContext();
    }

    @Override
    public void save(ShoppingList shoppingList) {
        context.newRecord(SHOPPING_LIST, shoppingList).merge();

        shoppingList.getShoppingListItems()
                    .forEach(shoppingListItem -> context.newRecord(SHOPPING_LIST_ITEM, ShoppingListItemMapper.unmap(shoppingListItem))
                                                        .merge());

        Condition shoppingListItemsRemoved = SHOPPING_LIST_ITEM.SHOPPING_LIST_REFERENCE.eq(shoppingList.getName());
        if (shoppingList.getShoppingListItems().size() > 0) {
            shoppingListItemsRemoved = shoppingListItemsRemoved.and(SHOPPING_LIST_ITEM.ID.notIn(shoppingList.getShoppingListItems()
                                                                                                            .stream()
                                                                                                            .map(ShoppingListItem::getId)
                                                                                                            .collect(Collectors.toList())));
        }
        context.delete(SHOPPING_LIST_ITEM).where(shoppingListItemsRemoved).execute();
    }

    @Override
    public Optional<ShoppingList> findByListName(String listName) {
        return context.fetch(JOINED_TABLE, SHOPPING_LIST.NAME.eq(listName))
                      .stream()
                      .collect(collector.toOptional());
    }

    @Override
    public List<ShoppingList> getAll() {
        return context.fetch(JOINED_TABLE)
                      .stream()
                      .collect(collector.toList(SHOPPING_LIST.NAME));
    }

    @Override
    public void delete(ShoppingList shoppingList) {
        context.newRecord(SHOPPING_LIST, shoppingList).delete();
    }
}
