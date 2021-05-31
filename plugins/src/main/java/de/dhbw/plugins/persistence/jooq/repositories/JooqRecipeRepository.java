package de.dhbw.plugins.persistence.jooq.repositories;

import de.dhbw.adapter.persistence.jooq.generated.Tables;
import de.dhbw.adapter.persistence.jooq.generated.tables.records.RecipeTagRecord;
import de.dhbw.adapter.persistence.jooq.mapper.collectors.RecipeCollector;
import de.dhbw.adapter.persistence.jooq.mapper.records.IngredientMapper;
import de.dhbw.aggregates.Recipe;
import de.dhbw.aggregates.Tag;
import de.dhbw.entities.Ingredient;
import de.dhbw.plugins.persistence.jooq.configuration.JooqConnection;
import de.dhbw.repositories.RecipeRepository;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JooqRecipeRepository implements RecipeRepository {

    private final DSLContext context;
    private final RecipeCollector collector = new RecipeCollector();

    private static final Table<Record> JOINED_TABLE = Tables.RECIPE.leftJoin(Tables.INGREDIENT)
                                                                   .on(Tables.INGREDIENT.RECIPE_REFERENCE.eq(Tables.RECIPE.ID))
                                                                   .leftJoin(Tables.RECIPE_TAG)
                                                                   .on(Tables.RECIPE_TAG.RECIPE_REFERENCE.eq(Tables.RECIPE.ID))
                                                                   .leftJoin(Tables.TAG)
                                                                   .on(Tables.TAG.NAME.eq(Tables.RECIPE_TAG.TAG_REFERENCE));

    @Autowired
    public JooqRecipeRepository(JooqConnection connection) {
        this.context = connection.getContext();
    }

    @Override
    public void save(Recipe recipe) {
        context.newRecord(Tables.RECIPE, recipe).merge();

        saveIngredients(recipe);
        deleteRemovedIngredients(recipe);

        updateTags(recipe);
    }

    private void saveIngredients(Recipe recipe) {
        IngredientMapper.extractRecords(recipe)
                        .forEach(ingredientRecord -> context.newRecord(Tables.INGREDIENT, ingredientRecord).merge());
    }

    private void deleteRemovedIngredients(Recipe recipe) {
        Condition ingredientsRemoved = Tables.INGREDIENT.RECIPE_REFERENCE.eq(recipe.getId().toString());
        if (recipe.getIngredients().size() > 0) {
            ingredientsRemoved = ingredientsRemoved.and(Tables.INGREDIENT.ID.notIn(recipe.getIngredients()
                                                                                         .stream()
                                                                                         .map(Ingredient::getId)
                                                                                         .toList()));
        }
        context.delete(Tables.INGREDIENT).where(ingredientsRemoved).execute();
    }

    private void updateTags(Recipe recipe) {
        List<String> alreadySavedTags = context.fetch(Tables.RECIPE_TAG, Tables.RECIPE_TAG.RECIPE_REFERENCE.eq(recipe.getId().toString()))
                                               .into(Tables.RECIPE_TAG.TAG_REFERENCE)
                                               .into(String.class);
        saveTags(recipe, alreadySavedTags);
        deleteRemovedTagReferences(recipe, alreadySavedTags);

    }

    private void saveTags(Recipe recipe, List<String> alreadySavedTags) {
        recipe.getTags()
              .stream()
              .filter(tag -> !alreadySavedTags.contains(tag.getName()))
              .forEach(tag -> context.newRecord(Tables.RECIPE_TAG, new RecipeTagRecord(UUID.randomUUID().toString(),
                                                                                       recipe.getId().toString(),
                                                                                       tag.getName())).merge());
    }

    private void deleteRemovedTagReferences(Recipe recipe, List<String> alreadySavedTags) {
        List<String> existingTags = recipe.getTags()
                                          .stream()
                                          .map(Tag::getName)
                                          .toList();

        alreadySavedTags.stream()
                        .filter(tag -> !existingTags.contains(tag))
                        .forEach(tag -> context.deleteFrom(Tables.RECIPE_TAG)
                                               .where(Tables.RECIPE_TAG.RECIPE_REFERENCE.eq(recipe.getId().toString()))
                                               .and(Tables.RECIPE_TAG.TAG_REFERENCE.eq(tag))
                                               .execute());
    }

    @Override
    public Optional<Recipe> findByRecipeId(UUID id) {
        return context.fetch(JOINED_TABLE, Tables.RECIPE.ID.eq(id.toString()))
                      .stream()
                      .collect(collector.toOptional());
    }

    @Override
    public List<Recipe> findByRecipeNameLike(String name) {
        return context.fetch(JOINED_TABLE, Tables.RECIPE.NAME.containsIgnoreCase(name))
                      .stream()
                      .collect(collector.toList(Tables.RECIPE.ID));
    }

    @Override
    public List<Recipe> findByTag(Tag tag) {
        List<String> ids = context.fetch(Tables.TAG.leftJoin(Tables.RECIPE_TAG)
                                                   .on(Tables.RECIPE_TAG.TAG_REFERENCE.eq(Tables.TAG.NAME)),
                                         Tables.TAG.NAME.eq(tag.getName())
                                        )
                                  .map(record -> record.into(Tables.RECIPE_TAG.RECIPE_REFERENCE)
                                                       .into(String.class));
        return context.fetch(JOINED_TABLE, Tables.RECIPE.ID.in(ids))
                      .stream()
                      .collect(collector.toList(Tables.RECIPE.ID));
    }

    @Override
    public List<Recipe> getAll() {
        return context.fetch(JOINED_TABLE)
                      .stream()
                      .collect(collector.toList(Tables.RECIPE.ID));
    }

    @Override
    public void delete(Recipe recipe) {
        context.newRecord(Tables.RECIPE, recipe).delete();
    }
}
