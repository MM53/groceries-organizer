package org.example.plugins.jooq.configuration;

import org.example.adapter.persistence.jooq.mapper.records.IngredientMapper;
import org.example.adapter.persistence.jooq.mapper.records.ItemLocationMapper;
import org.example.adapter.persistence.jooq.mapper.records.MinimumAmountMapper;
import org.example.adapter.persistence.jooq.mapper.records.ShoppingListItemMapper;
import org.example.entities.Ingredient;
import org.example.entities.ItemLocation;
import org.example.entities.MinimumAmount;
import org.example.entities.ShoppingListItem;
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

        if (type == Ingredient.class) {
            return (RecordMapper<R, E>) IngredientMapper.createRecordMapper();
        }

        return new DefaultRecordMapper(recordType, type);
    }
}
