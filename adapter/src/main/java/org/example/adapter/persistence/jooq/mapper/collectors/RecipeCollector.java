package org.example.adapter.persistence.jooq.mapper.collectors;

import org.example.aggregates.Recipe;
import org.example.aggregates.Tag;
import org.example.entities.Ingredient;
import org.jooq.Record;

import static org.example.adapter.persistence.jooq.generated.Tables.*;

public class RecipeCollector extends RecordCollector<Recipe> {

    @Override
    Recipe newEntityFromRecord(Record record) {
        return record.into(RECIPE).into(Recipe.class);
    }

    @Override
    Recipe updateEntityFromRecord(Record record, Recipe recipe) {
        try {
            Ingredient ingredient = record.into(INGREDIENT).into(Ingredient.class);
            recipe.addIngredient(ingredient);
        } catch (Exception ignored) {
        }
        try {
            Tag tag = record.into(TAG).into(Tag.class);
            recipe.addTag(tag);
        } catch (Exception ignored) {
        }
        return recipe;
    }
}
