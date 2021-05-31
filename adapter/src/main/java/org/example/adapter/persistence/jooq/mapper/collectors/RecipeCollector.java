package org.example.adapter.persistence.jooq.mapper.collectors;

import org.example.aggregates.Recipe;
import org.example.aggregates.Tag;
import org.example.entities.Ingredient;
import org.jooq.Record;

import java.util.UUID;

import static org.example.adapter.persistence.jooq.generated.Tables.*;

public class RecipeCollector extends RecordCollector<Recipe> {

    @Override
    Recipe newEntityFromRecord(Record record) {
        return record.into(RECIPE).into(Recipe.class);
    }

    @Override
    Recipe updateEntityFromRecord(Record record, Recipe recipe) {
        if (record.get(INGREDIENT.ID) != null && recipe.findIngredient(UUID.fromString(record.get(INGREDIENT.ID))).isEmpty()) {
            recipe.addIngredient(record.into(INGREDIENT).into(Ingredient.class));
        }
        if (record.get(TAG.NAME) != null) {
            Tag tag = record.into(TAG).into(Tag.class);
            if (!recipe.getTags().contains(tag)) {
                recipe.addTag(tag);
            }
        }
        return recipe;
    }
}
