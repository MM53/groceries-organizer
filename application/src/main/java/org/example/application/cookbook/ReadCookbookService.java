package org.example.application.cookbook;

import org.example.application.exceptions.RecipeNotFoundException;
import org.example.application.exceptions.TagNotFoundException;
import org.example.entities.aggregateRoots.Recipe;
import org.example.entities.aggregateRoots.Tag;
import org.example.repositories.RecipeRepository;
import org.example.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReadCookbookService {

    private final RecipeRepository recipeRepository;
    private final TagRepository tagRepository;

    @Autowired
    public ReadCookbookService(RecipeRepository recipeRepository, TagRepository tagRepository) {
        this.recipeRepository = recipeRepository;
        this.tagRepository = tagRepository;
    }

    public List<Recipe> listItems() {
        return recipeRepository.getAll();
    }

    public Recipe getRecipe(UUID id) {
        return recipeRepository.findByRecipeId(id)
                               .orElseThrow(() -> new RecipeNotFoundException(id));
    }

    public List<Recipe> getRecipes(String name) {
        return recipeRepository.findByRecipeNameLike(name);
    }

    public List<Recipe> getRecipes(Tag tag) {
        return recipeRepository.findByTag(tag);
    }

    public List<Tag> listTags() {
        return tagRepository.getAll();
    }

    public Tag getTag(String name) {
        return tagRepository.findTagByName(name)
                            .orElseThrow(() -> new TagNotFoundException(name));
    }
}
