package org.example.entities.aggregateRoots;

import org.example.entities.ShoppingListItem;
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
        shoppingListItems = shoppingListItems.stream()
                                             .filter(shoppingListItem -> !shoppingListItem.getItemReference().equals(itemReference))
                                             .collect(Collectors.toSet());
    }

    public void removeShoppingListItem(ShoppingListItem shoppingListItem) {
        shoppingListItems.remove(shoppingListItem);
    }

    public void removeShoppingListItems(Set<ShoppingListItem> itemsToRemove) {
        itemsToRemove.forEach(this::removeShoppingListItem);
    }

    public void updateBoughtStateOfShoppingListItem(String itemReference, boolean bought) {
        shoppingListItems.stream()
                         .filter(shoppingListItem -> shoppingListItem.getItemReference().equals(itemReference))
                         .forEach(shoppingListItem -> shoppingListItem.setBought(bought));
    }

    public void updateAmountOfShoppingListItem(String itemReference, Amount amount) {
        itemUtilService.validate(itemReference, amount.getUnit().getType());

        shoppingListItems.stream()
                         .filter(shoppingListItem -> shoppingListItem.getItemReference().equals(itemReference))
                         .forEach(shoppingListItem -> shoppingListItem.setAmount(amount));
    }

    public Set<ShoppingListItem> getShoppingListItems() {
        return shoppingListItems;
    }
}
