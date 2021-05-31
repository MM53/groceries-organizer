package de.dhbw.plugins.persistence.jooq.mapper.collectors;

import de.dhbw.aggregates.Recipe;
import de.dhbw.aggregates.Tag;
import de.dhbw.entities.Ingredient;
import de.dhbw.plugins.persistence.jooq.generated.Tables;
import org.jooq.Record;

import java.util.UUID;

public class RecipeCollector extends RecordCollector<Recipe> {

    @Override
    Recipe newEntityFromRecord(Record record) {
        return record.into(Tables.RECIPE).into(Recipe.class);
    }

    @Override
    Recipe updateEntityFromRecord(Record record, Recipe recipe) {
        if (record.get(Tables.INGREDIENT.ID) != null && recipe.findIngredient(UUID.fromString(record.get(Tables.INGREDIENT.ID))).isEmpty()) {
            recipe.addIngredient(record.into(Tables.INGREDIENT).into(Ingredient.class));
        }
        if (record.get(Tables.TAG.NAME) != null) {
            Tag tag = record.into(Tables.TAG).into(Tag.class);
            if (!recipe.getTags().contains(tag)) {
                recipe.addTag(tag);
            }
        }
        return recipe;
    }
}
