package org.example.application.cookbook;

import org.example.aggregates.Recipe;
import org.example.aggregates.Tag;
import org.example.entities.Ingredient;
import org.example.repositories.RecipeRepository;
import org.example.valueObjects.Amount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UpdateRecipeService {

    private final ReadCookbookService readCookbookService;
    private final RecipeRepository recipeRepository;

    @Autowired
    public UpdateRecipeService(ReadCookbookService readCookbookService, RecipeRepository recipeRepository) {
        this.readCookbookService = readCookbookService;
        this.recipeRepository = recipeRepository;
    }

    public void setName(UUID recipeId, String name) {
        Recipe recipe = readCookbookService.getRecipe(recipeId);
        recipe.setName(name);
        recipeRepository.save(recipe);
    }

    public void setDescription(UUID recipeId, String description) {
        Recipe recipe = readCookbookService.getRecipe(recipeId);
        recipe.setDescription(description);
        recipeRepository.save(recipe);
    }

    public void addIngredient(UUID recipeId, String itemName, Amount amount) {
        Recipe recipe = readCookbookService.getRecipe(recipeId);
        Optional<Ingredient> ingredient = recipe.findIngredient(itemName);
        if (ingredient.isPresent()) {
            recipe.updateIngredientAmount(ingredient.get().getId(), ingredient.get().getAmount().add(amount));
        } else {
            recipe.addIngredient(itemName, amount);
        }
        recipeRepository.save(recipe);
    }

    public void removeIngredient(UUID recipeId, UUID ingredientId) {
        Recipe recipe = readCookbookService.getRecipe(recipeId);
        recipe.removeIngredient(ingredientId);
        recipeRepository.save(recipe);
    }

    public void updateAmount(UUID recipeId, UUID ingredientId, Amount amount) {
        Recipe recipe = readCookbookService.getRecipe(recipeId);
        recipe.updateIngredientAmount(ingredientId, amount);
        recipeRepository.save(recipe);
    }

    public void addTag(UUID recipeId, String tagName) {
        Recipe recipe = readCookbookService.getRecipe(recipeId);
        Tag tag = readCookbookService.getTag(tagName);
        recipe.addTag(tag);
        recipeRepository.save(recipe);
    }

    public void removeTag(UUID recipeId, String tagName) {
        Recipe recipe = readCookbookService.getRecipe(recipeId);
        Tag tag = readCookbookService.getTag(tagName);
        recipe.removeTag(tag);
        recipeRepository.save(recipe);
    }

    public void updateTags(UUID recipeId, List<String> tagNames) {
        Recipe recipe = readCookbookService.getRecipe(recipeId);
        new ArrayList<>(recipe.getTags())
                .stream()
                .filter(tag -> !tagNames.contains(tag.getName()))
                .forEach(recipe::removeTag);
        tagNames.stream()
                .map(Tag::new)
                .filter(tag -> !recipe.getTags().contains(tag))
                .forEach(recipe::addTag);
        recipeRepository.save(recipe);
    }

}
