package org.example.entities.aggregateRoots;

import org.example.entities.ShoppingListItem;
import org.example.exceptions.ShoppingListItemNotFoundException;
import org.example.services.ItemUtilService;
import org.example.valueObjects.Amount;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ShoppingList {

    private final String name;
    private Set<ShoppingListItem> shoppingListItems;

    private final ItemUtilService itemUtilService = new ItemUtilService();

    public ShoppingList(String listName) {
        this(listName, new HashSet<>());
    }

    public ShoppingList(String listName, Set<ShoppingListItem> shoppingListItems) {
        this.name = listName;
        this.shoppingListItems = shoppingListItems;
    }

    public String getName() {
        return name;
    }

    public void addShoppingListItem(ShoppingListItem shoppingListItem) {
        itemUtilService.validateExistence(shoppingListItem.getItemReference());
        itemUtilService.validate(shoppingListItem.getItemReference(), shoppingListItem.getAmount().getUnit().getType());

        shoppingListItems.stream()
                         .filter(existingShoppingListItem -> shoppingListItem.getItemReference().equals(existingShoppingListItem.getItemReference()))
                         .findAny()
                         .ifPresentOrElse(existingItem -> existingItem.addAmount(shoppingListItem.getAmount()),
                                          () -> shoppingListItems.add(shoppingListItem));
    }

    public void addShoppingListItem(String itemReference, Amount amount) {
        addShoppingListItem(new ShoppingListItem(this.name, itemReference, amount));
    }

    public void removeShoppingListItem(String itemReference) {
        removeShoppingListItem(findShoppingListItem(itemReference));
    }

    public void removeShoppingListItem(ShoppingListItem shoppingListItem) {
        if (!shoppingListItems.remove(shoppingListItem)) {
            throw new ShoppingListItemNotFoundException(shoppingListItem.getItemReference());
        };
    }

    public void removeShoppingListItems(Set<ShoppingListItem> itemsToRemove) {
        itemsToRemove.forEach(this::removeShoppingListItem);
    }

    public void updateBoughtStateOfShoppingListItem(String itemReference, boolean bought) {
        ShoppingListItem entry = findShoppingListItem(itemReference);
        entry.setBought(bought);
    }

    public void updateAmountOfShoppingListItem(String itemReference, Amount amount) {
        itemUtilService.validate(itemReference, amount.getUnit().getType());

        ShoppingListItem entry = findShoppingListItem(itemReference);
        entry.setAmount(amount);
    }

    public Set<ShoppingListItem> getShoppingListItems() {
        return shoppingListItems;
    }

    private ShoppingListItem findShoppingListItem(String itemReference) {
        return shoppingListItems.stream()
                                .filter(shoppingListItem -> shoppingListItem.getItemReference().equals(itemReference))
                                .findAny()
                                .orElseThrow(() -> new ShoppingListItemNotFoundException(itemReference));
    }
}
