package org.example.aggregates;

import org.example.entities.Ingredient;
import org.example.exceptions.IngredientAlreadyExistsException;
import org.example.exceptions.IngredientNotFoundException;
import org.example.services.ItemUtilService;
import org.example.valueObjects.Amount;

import java.util.*;
import java.util.stream.Collectors;

public class Recipe {

    private final UUID id;
    private String name;
    private Set<Tag> tags;
    private Set<Ingredient> ingredients;
    private String description;

    private final ItemUtilService itemUtilService = new ItemUtilService();

    public Recipe(String name) {
        this(UUID.randomUUID(), name, new HashSet<>(), new HashSet<>(), "");
    }

    public Recipe(UUID id, String name) {
        this(id, name, new HashSet<>(), new HashSet<>(), "");
    }

    public Recipe(UUID id, String name, String description) {
        this(id, name, new HashSet<>(), new HashSet<>(), description);
    }

    public Recipe(UUID id, String name, Set<Tag> tags, Set<Ingredient> ingredients, String description) {
        this.id = id;
        this.name = name;
        this.tags = tags;
        this.ingredients = ingredients;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void addTag(Tag tag) {
        if (!tags.add(tag)) {
            throw new RuntimeException();
        }
    }

    public void removeTag(Tag tag) {
        if (!tags.remove(tag)) {
            throw new RuntimeException();
        }
    }

    public void addIngredient(Ingredient ingredient) {
        itemUtilService.validateExistence(ingredient.getItemReference());
        itemUtilService.validateUnit(ingredient.getItemReference(), ingredient.getAmount().getUnit().getType());
        if (findIngredient(ingredient.getId()).or(() -> findIngredient(ingredient.getItemReference())).isPresent()) {
            throw new IngredientAlreadyExistsException(ingredient.getId(), name);
        }

        ingredients.add(ingredient);
    }

    public void addIngredient(String itemReference, Amount amount) {
        addIngredient(new Ingredient(itemReference, amount));
    }

    public void removeIngredient(UUID ingredientId) {
        findIngredient(ingredientId).ifPresent(ingredients::remove);
    }

    public void updateIngredientAmount(UUID ingredientId, Amount amount) {
        ingredients.stream()
                   .filter(ingredient -> ingredient.getId().equals(ingredientId))
                   .findAny()
                   .orElseThrow(() -> new IngredientNotFoundException(ingredientId, name))
                   .setAmount(amount);
    }

    public Optional<Ingredient> findIngredient(UUID ingredientId) {
        return ingredients.stream()
                          .filter(ingredient -> ingredient.getId().equals(ingredientId))
                          .map(Ingredient::copy)
                          .findAny();
    }

    public Optional<Ingredient> findIngredient(String itemReference) {
        return ingredients.stream()
                          .filter(ingredient -> ingredient.getItemReference().equals(itemReference))
                          .map(Ingredient::copy)
                          .findAny();
    }

    public Set<Ingredient> getIngredients() {
        return ingredients.stream()
                          .map(Ingredient::copy)
                          .collect(Collectors.toSet());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return id.equals(recipe.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
