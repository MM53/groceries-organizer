package org.example.plugins.persistence.jooq.repositories;

import org.example.adapter.persistence.jooq.generated.tables.records.RecipeTagRecord;
import org.example.adapter.persistence.jooq.mapper.collectors.RecipeCollector;
import org.example.adapter.persistence.jooq.mapper.records.IngredientMapper;
import org.example.aggregates.Recipe;
import org.example.aggregates.Tag;
import org.example.entities.Ingredient;
import org.example.plugins.persistence.jooq.configuration.JooqConnection;
import org.example.repositories.RecipeRepository;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.example.adapter.persistence.jooq.generated.Tables.*;

@Component
public class JooqRecipeRepository implements RecipeRepository {

    private final DSLContext context;
    private final RecipeCollector collector = new RecipeCollector();

    private static final Table<Record> JOINED_TABLE = RECIPE.leftJoin(INGREDIENT)
                                                            .on(INGREDIENT.RECIPE_REFERENCE.eq(RECIPE.ID))
                                                            .leftJoin(RECIPE_TAG)
                                                            .on(RECIPE_TAG.RECIPE_REFERENCE.eq(RECIPE.ID));

    @Autowired
    public JooqRecipeRepository(JooqConnection connection) {
        this.context = connection.getContext();
    }

    @Override
    public void save(Recipe recipe) {
        context.newRecord(RECIPE, recipe).merge();

        List<String> referencesTags = context.fetch(RECIPE_TAG, RECIPE_TAG.RECIPE_REFERENCE.eq(recipe.getId().toString()))
                                             .into(RECIPE_TAG.TAG_REFERENCE)
                                             .into(String.class);

        recipe.getTages()
              .stream()
              .filter(tag -> !referencesTags.contains(tag.getName()))
              .forEach(tag -> context.newRecord(RECIPE_TAG, new RecipeTagRecord(UUID.randomUUID().toString(),
                                                                                recipe.getId().toString(),
                                                                                tag.getName())));
        List<String> existingTags = recipe.getTages()
                                          .stream()
                                          .map(Tag::getName)
                                          .toList();

        referencesTags.stream()
                      .filter(tag -> !existingTags.contains(tag))
                      .forEach(tag -> context.deleteFrom(RECIPE_TAG)
                                             .where(RECIPE_TAG.RECIPE_REFERENCE.eq(recipe.getId().toString()))
                                             .and(RECIPE_TAG.TAG_REFERENCE.eq(tag)));

        IngredientMapper.extractRecords(recipe)
                        .forEach(ingredientRecord -> context.newRecord(INGREDIENT, ingredientRecord).merge());

        Condition ingredientsRemoved = INGREDIENT.RECIPE_REFERENCE.eq(recipe.getId().toString());
        if (recipe.getIngredients().size() > 0) {
            ingredientsRemoved = ingredientsRemoved.and(INGREDIENT.ID.notIn(recipe.getIngredients()
                                                                                  .stream()
                                                                                  .map(Ingredient::getId)
                                                                                  .toList()));
        }
        context.delete(INGREDIENT).where(ingredientsRemoved).execute();
    }

    @Override
    public Optional<Recipe> findByRecipeId(UUID id) {
        return context.fetch(JOINED_TABLE, RECIPE.ID.eq(id.toString()))
                      .stream()
                      .collect(collector.toOptional());
    }

    @Override
    public List<Recipe> findByRecipeNameLike(String name) {
        return context.fetch(JOINED_TABLE, RECIPE.NAME.containsIgnoreCase(name))
                      .stream()
                      .collect(collector.toList(RECIPE.ID));
    }

    @Override
    public List<Recipe> findByTag(Tag tag) {
        List<String> ids = context.fetch(TAG.where(TAG.NAME.eq(tag.getName()))
                                            .leftJoin(RECIPE_TAG)
                                            .on(RECIPE_TAG.TAG_REFERENCE.eq(TAG.NAME)))
                                  .map(record -> record.into(RECIPE_TAG.RECIPE_REFERENCE)
                                                       .into(String.class));
        return context.fetch(JOINED_TABLE, RECIPE.ID.in(ids))
                      .stream()
                      .collect(collector.toList(RECIPE.ID));
    }

    @Override
    public List<Recipe> getAll() {
        return context.fetch(JOINED_TABLE)
                      .stream()
                      .collect(collector.toList(RECIPE.ID));
    }

    @Override
    public void delete(Recipe recipe) {
        context.newRecord(RECIPE, recipe).delete();
    }
}
