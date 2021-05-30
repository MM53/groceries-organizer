package org.example.adapter.persistence.jooq.mapper.collectors;

import org.example.aggregates.ShoppingList;
import org.example.entities.ShoppingListItem;
import org.jooq.Record;

import static org.example.adapter.persistence.jooq.generated.Tables.SHOPPING_LIST;
import static org.example.adapter.persistence.jooq.generated.Tables.SHOPPING_LIST_ITEM;

public class ShoppingListCollector extends RecordCollector<ShoppingList> {

    @Override
    ShoppingList newEntityFromRecord(Record record) {
        return new ShoppingList(record.get(SHOPPING_LIST.NAME));
    }

    @Override
    ShoppingList updateEntityFromRecord(Record record, ShoppingList shoppingList) {
        if (record.get(SHOPPING_LIST_ITEM.ID) != null) {
            shoppingList.addShoppingListItem(record.into(SHOPPING_LIST_ITEM).into(ShoppingListItem.class));
        }
        return shoppingList;
    }
}
