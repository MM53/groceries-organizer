package de.dhbw.plugins.persistence.jooq.mapper.collectors;

import de.dhbw.aggregates.Item;
import de.dhbw.entities.ItemName;
import de.dhbw.plugins.persistence.jooq.generated.Tables;
import org.jooq.Record;

public class ItemCollector extends RecordCollector<Item> {

    @Override
    Item newEntityFromRecord(Record record) {
        return record.into(Tables.ITEM).into(Item.class);
    }

    @Override
    Item updateEntityFromRecord(Record record, Item item) {
        item.addAlternativeName(record.into(Tables.ITEM_NAME).into(ItemName.class));
        return item;
    }
}
