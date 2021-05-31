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
        if (record.get(INGREDIENT.ID) != null){
            recipe.addIngredient(record.into(INGREDIENT).into(Ingredient.class));
        }
        if (record.get(TAG.NAME) != null) {
            recipe.addTag(record.into(TAG).into(Tag.class));
        }
        return recipe;
    }
}
