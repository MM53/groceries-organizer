package de.dhbw.application.cookbook;

import de.dhbw.aggregates.StoredItem;
import de.dhbw.application.exceptions.MissingIngredientsException;
import de.dhbw.application.exceptions.StoredItemNotFoundException;
import de.dhbw.application.shoppingList.UpdateShoppingListEntriesService;
import de.dhbw.application.storage.ReadStorageService;
import de.dhbw.application.storage.TakeAmountService;
import de.dhbw.entities.Ingredient;
import de.dhbw.entities.ItemLocation;
import de.dhbw.valueObjects.Amount;
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
                                                                    .allMatch(ingredient -> ingredient.getAmount().isEmpty());
        if (!allIngredientsAvailable) {
            throw new MissingIngredientsException(readCookbookService.getRecipe(recipeId));
        }

        readCookbookService.getRecipe(recipeId)
                           .getIngredients()
                           .forEach(this::takeIngredientFromStorage);
    }

    private Ingredient checkIngredient(Ingredient ingredient) {
        try {
            StoredItem storedItem = readStorageService.getStoredItem(ingredient.getItemReference());
            if (storedItem.getTotalAmount().isMoreThan(ingredient.getAmount())) {
                ingredient.setAmount(new Amount(0, ingredient.getAmount().getUnit()));
            } else {
                ingredient.setAmount(storedItem.getTotalAmount().sub(ingredient.getAmount()));
            }
        } catch (StoredItemNotFoundException e) {
            ingredient.setAmount(new Amount(-ingredient.getAmount().getValue(), ingredient.getAmount().getUnit()));
        }
        return ingredient;
    }

    private void planIngredient(Ingredient ingredient) {
        try {
            StoredItem storedItem = readStorageService.getStoredItem(ingredient.getItemReference());
            if (ingredient.getAmount().isMoreThan(storedItem.getTotalAmount())) {
                updateShoppingListEntriesService.addEntry(ingredient.getItemReference(),
                                                          ingredient.getAmount().sub(storedItem.getTotalAmount()));
                ingredient.setAmount(storedItem.getTotalAmount());
            }
            takeIngredientFromStorage(ingredient);
        } catch (StoredItemNotFoundException e) {
            updateShoppingListEntriesService.addEntry(ingredient.getItemReference(), ingredient.getAmount());
        }
    }

    private void takeIngredientFromStorage(Ingredient ingredient) {
        List<ItemLocation> itemLocations = readStorageService.listItemLocations(ingredient.getItemReference());
        Iterator<ItemLocation> itemLocationIterator = itemLocations.iterator();

        Amount amountToTake = ingredient.getAmount();
        while (!amountToTake.isEmpty() && itemLocationIterator.hasNext()) {
            ItemLocation currentItemLocation = itemLocationIterator.next();
            amountToTake = takeAmountService.takeAmount(ingredient.getItemReference(), amountToTake, currentItemLocation.getId());
        }
        if (!amountToTake.isEmpty()) {
            throw new MissingIngredientsException(ingredient);
        }
    }
}
