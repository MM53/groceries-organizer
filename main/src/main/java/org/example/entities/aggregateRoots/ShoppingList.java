package org.example.entities.aggregateRoots;

import org.example.entities.ShoppingListItem;
import org.example.services.ItemUtilService;
import org.example.valueObjects.Amount;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ShoppingList {

    private final String listName;
    private Set<ShoppingListItem> shoppingListItems;

    private final ItemUtilService itemUtilService = new ItemUtilService();

    public ShoppingList(String listName) {
        this(listName, new HashSet<>());
    }

    public ShoppingList(String listName, Set<ShoppingListItem> shoppingListItems) {
        this.listName = listName;
        this.shoppingListItems = shoppingListItems;
    }

    public String getListName() {
        return listName;
    }

    public void addShoppingListItem(ShoppingListItem shoppingListItem) {
        shoppingListItems.stream()
                         .filter(existingShoppingListItem -> shoppingListItem.getItemReference().equals(existingShoppingListItem.getItemReference()))
                         .findAny()
                         .ifPresentOrElse(
                                 existingShoppingListItem -> existingShoppingListItem.addAmount(shoppingListItem.getAmount()),
                                 () -> shoppingListItems.add(shoppingListItem)
                                         );
    }

    public void addShoppingListItem(String itemReference, Amount amount) {
        addShoppingListItem(new ShoppingListItem(itemReference, amount));
    }

    public void buyShoppingListItem(String itemReference) {
        shoppingListItems.stream()
                         .filter(shoppingListItem -> shoppingListItem.getItemReference().equals(itemReference))
                         .forEach(shoppingListItem -> shoppingListItem.setBought(true));
    }

    public void clearBoughtShoppingListItems() {
        shoppingListItems = shoppingListItems.stream()
                                             .filter(ShoppingListItem::isBought)
                                             .collect(Collectors.toSet());
    }

    public Set<ShoppingListItem> getShoppingListItems() {
        return shoppingListItems;
    }
}
