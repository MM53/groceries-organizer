package de.dhbw.repositories;

import de.dhbw.aggregates.Recipe;
import de.dhbw.aggregates.Tag;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecipeRepository {

    public void save(Recipe recipe);

    public Optional<Recipe> findByRecipeId(UUID id);

    public List<Recipe> findByRecipeNameLike(String name);

    public List<Recipe> findByTag(Tag tag);

    public List<Recipe> getAll();

    public void delete(Recipe recipe);
}
