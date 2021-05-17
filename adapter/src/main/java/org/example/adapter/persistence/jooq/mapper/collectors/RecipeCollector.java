package org.example.adapter.persistence.jooq.mapper.collectors;

import org.example.entities.Ingredient;
import org.example.entities.ItemName;
import org.example.entities.aggregateRoots.Item;
import org.example.entities.aggregateRoots.Recipe;
import org.example.entities.aggregateRoots.Tag;
import org.jooq.Record;

import java.util.Optional;

import static org.example.adapter.persistence.jooq.generated.Tables.*;

public class RecipeCollector extends RecordCollector<Recipe> {

    @Override
    Recipe newEntityFromRecord(Record record) {
        return record.into(RECIPE).into(Recipe.class);
    }

    @Override
    Recipe updateEntityFromRecord(Record record, Recipe recipe) {
        Optional.ofNullable(record.into(INGREDIENT).into(Ingredient.class))
                .ifPresent(recipe::addIngredient);
        Optional.ofNullable(record.into(TAG).into(Tag.class))
                .ifPresent(recipe::addTag);
        return recipe;
    }
}
