package org.example.persistence.jooq.configuration;

import org.example.entities.ItemLocation;
import org.example.entities.MinimumAmount;
import org.example.entities.ShoppingListItem;
import org.example.persistence.jooq.mapper.records.ItemLocationMapper;
import org.example.persistence.jooq.mapper.records.MinimumAmountMapper;
import org.example.persistence.jooq.mapper.records.ShoppingListItemMapper;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.RecordMapperProvider;
import org.jooq.RecordType;
import org.jooq.impl.DefaultRecordMapper;

public class CustomRecordMapperProvider implements RecordMapperProvider {
    @Override
    public <R extends Record, E> RecordMapper<R, E> provide(RecordType<R> recordType, Class<? extends E> type) {

        if (type == ItemLocation.class) {
            return (RecordMapper<R, E>) ItemLocationMapper.createRecordMapper();
        }

        if (type == MinimumAmount.class) {
            return (RecordMapper<R, E>) MinimumAmountMapper.createRecordMapper();
        }

        if (type == ShoppingListItem.class) {
            return (RecordMapper<R, E>) ShoppingListItemMapper.createRecordMapper();
        }

        return new DefaultRecordMapper(recordType, type);
    }
}
