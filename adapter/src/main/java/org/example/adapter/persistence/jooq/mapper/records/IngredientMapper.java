package org.example.adapter.persistence.jooq.mapper.records;

import org.example.adapter.persistence.jooq.generated.tables.records.IngredientRecord;
import org.example.aggregates.Recipe;
import org.example.entities.Ingredient;
import org.example.units.Pieces;
import org.example.units.UnitType;
import org.example.units.Volume;
import org.example.units.Weight;
import org.example.valueObjects.Amount;
import org.jooq.RecordMapper;

import java.util.List;
import java.util.UUID;

public class IngredientMapper {
    public static RecordMapper<IngredientRecord, Ingredient> createRecordMapper() {
        return record -> {
            Amount amount = switch (UnitType.valueOf(record.getAmountUnitType())) {
                case PIECES -> new Amount(record.getAmountValue(), Pieces.PIECES);
                case VOLUME -> new Amount(record.getAmountValue(), Volume.valueOf(record.getAmountUnit()));
                case WEIGHT -> new Amount(record.getAmountValue(), Weight.valueOf(record.getAmountUnit()));
            };

            return new Ingredient(UUID.fromString(record.getId()),
                                  record.getItemReference(),
                                  amount);
        };
    }

    public static List<IngredientRecord> extractRecords(Recipe recipe) {
        return recipe.getIngredients()
                     .stream()
                     .map(ingredient -> new IngredientRecord(ingredient.getId().toString(),
                                                             recipe.getId().toString(),
                                                             ingredient.getItemReference(),
                                                             ingredient.getAmount().getValue(),
                                                             ingredient.getAmount().getUnit().name(),
                                                             ingredient.getAmount().getUnit().getType().toString()))
                     .toList();
    }
}
