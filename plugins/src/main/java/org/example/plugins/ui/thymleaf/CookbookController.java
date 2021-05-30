package org.example.plugins.ui.thymleaf;

import org.example.adapter.ui.ItemAmountTupelMapper;
import org.example.aggregates.Recipe;
import org.example.application.cookbook.ManageCookbookService;
import org.example.application.cookbook.ReadCookbookService;
import org.example.application.cookbook.UpdateRecipeService;
import org.example.application.cookbook.UseRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Arrays;
import java.util.UUID;

@Controller
public class CookbookController {

    private final ReadCookbookService readCookbookService;
    private final ManageCookbookService manageCookbookService;
    private final UseRecipeService useRecipeService;
    private final UpdateRecipeService updateRecipeService;
    private final ItemAmountTupelMapper itemAmountTupelMapper;

    @Autowired
    public CookbookController(ReadCookbookService readCookbookService,
                              ManageCookbookService manageCookbookService,
                              UseRecipeService useRecipeService,
                              UpdateRecipeService updateRecipeService,
                              ItemAmountTupelMapper itemAmountTupelMapper) {
        this.readCookbookService = readCookbookService;
        this.manageCookbookService = manageCookbookService;
        this.useRecipeService = useRecipeService;
        this.updateRecipeService = updateRecipeService;
        this.itemAmountTupelMapper = itemAmountTupelMapper;
    }

    @GetMapping("/cookbook/recipes")
    public String listRecipes(Model model) {
        model.addAttribute("template", "recipesList");
        model.addAttribute("recipes", readCookbookService.listRecipes());
        return "layout/main";
    }

    @GetMapping("/cookbook/recipes/{recipe-id}")
    public String showRecipe(@PathVariable("recipe-id") String recipeId, Model model) {
        model.addAttribute("template", "recipe");
        model.addAttribute("recipe", readCookbookService.getRecipe(UUID.fromString(recipeId)));
        return "layout/main";
    }

    @GetMapping("/cookbook/recipes/create")
    public String createRecipe(Model model) {
        model.addAttribute("template", "editRecipe");
        model.addAttribute("recipe", new Recipe(null, ""));
        return "layout/main";
    }

    @GetMapping("/cookbook/recipes/edit/{recipe-id}")
    public String editRecipe(@PathVariable("recipe-id") String recipeId, Model model) {
        model.addAttribute("template", "editRecipe");
        model.addAttribute("recipe", readCookbookService.getRecipe(UUID.fromString(recipeId)));
        return "layout/main";
    }

    @PostMapping("/cookbook/recipes")
    public RedirectView saveRecipe(@RequestParam(name = "recipe-id", required = false) String recipeIdString,
                                   @RequestParam("recipe-name") String recipeName,
                                   @RequestParam String description,
                                   @RequestParam("added-ingredients") String addedIngredients,
                                   @RequestParam("removed-ingredients") String removedIngredients) {
        UUID recipeId;
        if (recipeIdString == null) {
            recipeId = manageCookbookService.createRecipe(recipeName).getId();
        } else {
            recipeId = UUID.fromString(recipeIdString);
            updateRecipeService.setName(recipeId, recipeName);
        }

        updateRecipeService.setDescription(recipeId, description);
        Arrays.stream(addedIngredients.split(","))
              .filter(ingredient -> !ingredient.equals(""))
              .map(itemAmountTupelMapper::extractFromString)
              .forEach(itemAmountTupel -> updateRecipeService.addIngredient(recipeId, itemAmountTupel.getItem(), itemAmountTupel.getAmount()));
        Arrays.stream(removedIngredients.split(","))
              .filter(ingredient -> !ingredient.equals(""))
              .forEach(ingredient -> updateRecipeService.removeIngredient(recipeId, UUID.fromString(ingredient)));
        return new RedirectView("/cookbook/recipes/" + recipeId);
    }
}
