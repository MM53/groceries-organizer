package org.example.application.cookbook;

import org.example.aggregates.StoredItem;
import org.example.application.shoppingList.UpdateShoppingListEntriesService;
import org.example.application.storage.ReadStorageService;
import org.example.application.storage.TakeAmountService;
import org.example.entities.Ingredient;
import org.example.entities.ItemLocation;
import org.example.valueObjects.Amount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
public class UseRecipeService {

    private final ReadCookbookService readCookbookService;
    private final ReadStorageService readStorageService;
    private final TakeAmountService takeAmountService;
    private final UpdateShoppingListEntriesService updateShoppingListEntriesService;

    @Autowired
    public UseRecipeService(ReadCookbookService readCookbookService,
                            ReadStorageService readStorageService,
                            TakeAmountService takeAmountService,
                            UpdateShoppingListEntriesService updateShoppingListEntriesService) {
        this.readCookbookService = readCookbookService;
        this.readStorageService = readStorageService;
        this.takeAmountService = takeAmountService;
        this.updateShoppingListEntriesService = updateShoppingListEntriesService;
    }

    public List<Ingredient> checkIngredients(UUID recipeId) {
        return readCookbookService.getRecipe(recipeId).getIngredients()
                                  .stream()
                                  .map(this::checkIngredient)
                                  .toList();
    }

    public void planRecipe(UUID recipeId) {
        readCookbookService.getRecipe(recipeId)
                           .getIngredients()
                           .forEach(this::planIngredient);
    }

    public void cookRecipe(UUID recipeId) {
        boolean allIngredientsAvailable = checkIngredients(recipeId).stream()
                                                                    .anyMatch(ingredient -> !ingredient.getAmount().isEmpty());
        if (!allIngredientsAvailable) {
            throw new RuntimeException();
        }

        readCookbookService.getRecipe(recipeId)
                           .getIngredients()
                           .forEach(this::takeIngredientFromStorage);
    }

    private Ingredient checkIngredient(Ingredient ingredient) {
        StoredItem storedItem = readStorageService.getStoredItem(ingredient.getItemReference());
        if (storedItem.getTotalAmount().isMoreThan(ingredient.getAmount())) {
            ingredient.setAmount(new Amount(0, ingredient.getAmount().getUnit()));
        } else {
            ingredient.setAmount(storedItem.getTotalAmount().sub(ingredient.getAmount()));
        }
        return ingredient;
    }

    private void planIngredient(Ingredient ingredient) {
        StoredItem storedItem = readStorageService.getStoredItem(ingredient.getItemReference());
        if (ingredient.getAmount().isMoreThan(storedItem.getTotalAmount())) {
            updateShoppingListEntriesService.addEntry(ingredient.getItemReference(),
                                                      ingredient.getAmount().sub(storedItem.getTotalAmount()));
            ingredient.setAmount(storedItem.getTotalAmount());
        }
        takeIngredientFromStorage(ingredient);
    }

    private void takeIngredientFromStorage(Ingredient ingredient) {
        List<ItemLocation> itemLocations = readStorageService.listItemLocations(ingredient.getItemReference());
        Iterator<ItemLocation> itemLocationIterator =  itemLocations.iterator();

        Amount amountToTake = ingredient.getAmount();
        while (!amountToTake.isEmpty() && itemLocationIterator.hasNext()) {
            ItemLocation currentItemLocation = itemLocationIterator.next();
            amountToTake = takeAmountService.takeAmount(ingredient.getItemReference(), amountToTake, currentItemLocation.getId());
        }
        if (!amountToTake.isEmpty()) {
            throw new RuntimeException();
        }
    }
}
