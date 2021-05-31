package org.example.aggregates;

import org.example.entities.ShoppingListItem;
import org.example.exceptions.ShoppingListItemAlreadyExistsException;
import org.example.exceptions.ShoppingListItemNotFoundException;
import org.example.services.ItemUtilService;
import org.example.valueObjects.Amount;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ShoppingList {

    private final String name;
    private Set<ShoppingListItem> shoppingListItems;

    private final ItemUtilService itemUtilService = new ItemUtilService();

    public static final String DEFAULT_SHOPPING_LIST = "default";

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
        itemUtilService.validateUnit(shoppingListItem.getItemReference(), shoppingListItem.getAmount().getUnit().getType());
        if (getOriginalShoppingListItem(shoppingListItem.getItemReference()).isPresent()) {
            throw new ShoppingListItemAlreadyExistsException(shoppingListItem.getItemReference());
        }

        shoppingListItems.add(shoppingListItem);
    }

    public void addShoppingListItem(String itemReference, Amount amount) {
        addShoppingListItem(new ShoppingListItem(itemReference, amount));
    }

    public void removeShoppingListItem(String itemReference) {
        getOriginalShoppingListItem(itemReference).ifPresent(this::removeShoppingListItem);
    }

    public void removeShoppingListItems(Set<ShoppingListItem> itemsToRemove) {
        itemsToRemove.forEach(this::removeShoppingListItem);
    }

    private void removeShoppingListItem(ShoppingListItem shoppingListItem) {
        if (!shoppingListItems.remove(shoppingListItem)) {
            throw new ShoppingListItemNotFoundException(shoppingListItem.getItemReference());
        }
    }

    public void updateBoughtStateOfShoppingListItem(String itemReference, boolean bought) {
        ShoppingListItem entry = getOriginalShoppingListItem(itemReference).orElseThrow(() -> new ShoppingListItemNotFoundException(itemReference));
        entry.setBought(bought);
    }

    public void updateAmountOfShoppingListItem(String itemReference, Amount amount) {
        itemUtilService.validateUnit(itemReference, amount.getUnit().getType());

        ShoppingListItem entry = getOriginalShoppingListItem(itemReference).orElseThrow(() -> new ShoppingListItemNotFoundException(itemReference));
        entry.setAmount(amount);
    }

    public Set<ShoppingListItem> getShoppingListItems() {
        return shoppingListItems.stream()
                                .map(ShoppingListItem::copy)
                                .collect(Collectors.toSet());
    }

    private Optional<ShoppingListItem> getOriginalShoppingListItem(String itemReference) {
        return shoppingListItems.stream()
                                .filter(shoppingListItem -> shoppingListItem.getItemReference().equals(itemReference))
                                .findAny();
    }

    public Optional<ShoppingListItem> findShoppingListItem(String itemReference) {
        return getOriginalShoppingListItem(itemReference).map(ShoppingListItem::copy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingList that = (ShoppingList) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
