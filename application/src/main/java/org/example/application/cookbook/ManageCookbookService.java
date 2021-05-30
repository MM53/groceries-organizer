package org.example.application.cookbook;

import org.example.aggregates.Recipe;
import org.example.aggregates.Tag;
import org.example.repositories.RecipeRepository;
import org.example.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ManageCookbookService {

    private final ReadCookbookService readCookbookService;
    private final RecipeRepository recipeRepository;
    private final TagRepository tagRepository;

    @Autowired
    public ManageCookbookService(ReadCookbookService readCookbookService, RecipeRepository recipeRepository, TagRepository tagRepository) {
        this.readCookbookService = readCookbookService;
        this.recipeRepository = recipeRepository;
        this.tagRepository = tagRepository;
    }

    public Recipe createRecipe(String name) {
        Recipe recipe = new Recipe(name);
        recipeRepository.save(recipe);
        return recipe;
    }

    public void deleteRecipe(UUID recipeId) {
        Recipe recipe = readCookbookService.getRecipe(recipeId);
        recipeRepository.delete(recipe);
    }

    public void createTag(String tagName) {
        tagRepository.save(new Tag(tagName));
    }

    public void deleteTag(String tagName) {
        tagRepository.delete(new Tag(tagName));
    }
}
