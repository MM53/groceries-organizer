package de.dhbw.application.cookbook;

import de.dhbw.aggregates.Recipe;
import de.dhbw.aggregates.Tag;
import de.dhbw.application.exceptions.RecipeNotFoundException;
import de.dhbw.application.exceptions.TagNotFoundException;
import de.dhbw.repositories.RecipeRepository;
import de.dhbw.repositories.TagRepository;
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

    public List<Recipe> listRecipes() {
        return recipeRepository.getAll();
    }

    public Recipe getRecipe(UUID id) {
        return recipeRepository.findByRecipeId(id)
                               .orElseThrow(() -> new RecipeNotFoundException(id));
    }

    public List<Recipe> searchByName(String name) {
        return recipeRepository.findByRecipeNameLike(name);
    }

    public List<Recipe> searchByTag(Tag tag) {
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
