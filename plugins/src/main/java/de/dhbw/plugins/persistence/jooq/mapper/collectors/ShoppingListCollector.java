package de.dhbw.plugins.persistence.jooq.mapper.collectors;

import de.dhbw.aggregates.ShoppingList;
import de.dhbw.entities.ShoppingListItem;
import de.dhbw.plugins.persistence.jooq.generated.Tables;
import org.jooq.Record;

public class ShoppingListCollector extends RecordCollector<ShoppingList> {

    @Override
    ShoppingList newEntityFromRecord(Record record) {
        return new ShoppingList(record.get(Tables.SHOPPING_LIST.NAME));
    }

    @Override
    ShoppingList updateEntityFromRecord(Record record, ShoppingList shoppingList) {
        if (record.get(Tables.SHOPPING_LIST_ITEM.ID) != null) {
            shoppingList.addShoppingListItem(record.into(Tables.SHOPPING_LIST_ITEM).into(ShoppingListItem.class));
        }
        return shoppingList;
    }
}
