package org.example.adapter.persistence.jooq.mapper.collectors;

import org.example.aggregates.Item;
import org.example.entities.ItemName;
import org.jooq.Record;

import static org.example.adapter.persistence.jooq.generated.Tables.ITEM;
import static org.example.adapter.persistence.jooq.generated.Tables.ITEM_NAME;

public class ItemCollector extends RecordCollector<Item> {

    @Override
    Item newEntityFromRecord(Record record) {
        return record.into(ITEM).into(Item.class);
    }

    @Override
    Item updateEntityFromRecord(Record record, Item item) {
        item.addAlternativeName(record.into(ITEM_NAME).into(ItemName.class));
        return item;
    }
}
