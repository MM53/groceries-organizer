package org.example.entities.aggregateRoots;

import org.example.entities.Ingredient;
import org.example.exceptions.IngredientAlreadyExistsException;
import org.example.exceptions.IngredientNotFoundException;
import org.example.services.ItemUtilService;
import org.example.valueObjects.Amount;

import java.util.*;

public class Recipe {

    private final UUID id;
    private String name;
    private Set<Tag> tages;
    private Set<Ingredient> ingredients;
    private String description;

    private final ItemUtilService itemUtilService = new ItemUtilService();

    public Recipe(String name) {
        this(UUID.randomUUID(), name, new HashSet<>(), new HashSet<>(), "");
    }

    public Recipe(UUID id, String name) {
        this(id, name, new HashSet<>(), new HashSet<>(), "");
    }

    public Recipe(UUID id, String name, Set<Tag> tages, Set<Ingredient> ingredients, String description) {
        this.id = id;
        this.name = name;
        this.tages = tages;
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

    public Set<Tag> getTages() {
        return tages;
    }

    public void addTag(Tag tag) {
        if (!tages.add(tag)) {
            throw new RuntimeException();
        }
    }

    public void removeTag(Tag tag) {
        if (!tages.remove(tag)) {
            throw new RuntimeException();
        }
    }

    public void addIngredient(Ingredient ingredient) {
        itemUtilService.validateExistence(ingredient.getItemReference());
        itemUtilService.validate(ingredient.getItemReference(), ingredient.getAmount().getUnit().getType());
        if (findIngredient(ingredient.getId()).or(() -> findIngredient(ingredient.getItemReference())).isPresent()) {
            throw new IngredientAlreadyExistsException(ingredient.getId(), name);
        }

        ingredients.add(ingredient);
    }

    public void addIngredient(String itemReference, Amount amount) {
        addIngredient(new Ingredient(id, itemReference, amount));
    }

    public void removeIngredient(UUID ingredientId) {
        findIngredient(ingredientId).ifPresent(ingredients::remove);
    }

    public void updateIngredientAmount(UUID ingredientId, Amount amount) {
        findIngredient(ingredientId).orElseThrow(() -> new IngredientNotFoundException(ingredientId, name))
                                    .setAmount(amount);
    }

    public Optional<Ingredient> findIngredient(UUID ingredientId) {
        return ingredients.stream()
                          .filter(ingredient -> ingredient.getId().equals(ingredientId))
                          .findAny();
    }

    public Optional<Ingredient> findIngredient(String itemReference) {
        return ingredients.stream()
                          .filter(ingredient -> ingredient.getItemReference().equals(itemReference))
                          .findAny();
    }

    public Set<Ingredient> getIngredients() {
        return ingredients;
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
