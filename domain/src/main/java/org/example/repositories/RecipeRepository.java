package org.example.repositories;

import org.example.entities.aggregateRoots.Tag;
import org.example.entities.aggregateRoots.Recipe;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecipeRepository {

    public void save(Recipe recipe);

    public Optional<Recipe> findByListId(UUID id);

    public Optional<Recipe> findByListName(String name);

    public List<Recipe> findByTag(Tag tag);

    public List<Recipe> getAll();

    public void delete(Recipe recipe);
}
