package de.dhbw.plugins.persistence.jooq.mapper.records;

import de.dhbw.aggregates.Recipe;
import de.dhbw.entities.Ingredient;
import de.dhbw.plugins.persistence.jooq.generated.tables.records.IngredientRecord;
import de.dhbw.units.Pieces;
import de.dhbw.units.UnitType;
import de.dhbw.units.Volume;
import de.dhbw.units.Weight;
import de.dhbw.valueObjects.Amount;
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
