package de.dhbw.plugins.persistence.jooq.configuration;

import de.dhbw.adapter.persistence.jooq.mapper.records.IngredientMapper;
import de.dhbw.adapter.persistence.jooq.mapper.records.ItemLocationMapper;
import de.dhbw.adapter.persistence.jooq.mapper.records.MinimumAmountMapper;
import de.dhbw.adapter.persistence.jooq.mapper.records.ShoppingListItemMapper;
import de.dhbw.entities.Ingredient;
import de.dhbw.entities.ItemLocation;
import de.dhbw.entities.MinimumAmount;
import de.dhbw.entities.ShoppingListItem;
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
