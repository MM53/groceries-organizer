package org.example.application;

import org.example.application.exceptions.ShoppingListAlreadyExistsException;
import org.example.application.exceptions.ShoppingListNotFoundException;
import org.example.entities.ShoppingListItem;
import org.example.entities.aggregateRoots.ShoppingList;
import org.example.repositories.ShoppingListRepository;
import org.example.valueObjects.Amount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ShoppingListManager {

    private final ShoppingListRepository shoppingListRepository;

    @Autowired
    public ShoppingListManager(ShoppingListRepository shoppingListRepository) {
        this.shoppingListRepository = shoppingListRepository;
    }

    private ShoppingList getShoppingList(String listName) {
        return shoppingListRepository.findByListName(listName)
                                     .orElseThrow(() -> new ShoppingListNotFoundException(listName));
    }

    public void createShoppingList(String name) {
        shoppingListRepository.findByListName(name)
                              .ifPresent(shoppingList -> {
                                  throw new ShoppingListAlreadyExistsException(name);
                              });
        ShoppingList shoppingList = new ShoppingList(name);
        shoppingListRepository.save(shoppingList);
    }

    public ShoppingList viewShoppingList(String listName) {
        return getShoppingList(listName);
    }

    public List<ShoppingListItem> listEntries(String listName) {
        return getShoppingList(listName).getShoppingListItems()
                                        .stream()
                                        .toList();
    }

    public void addEntry(String listName, String itemName, Amount amount) {
        ShoppingList shoppingList = getShoppingList(listName);
        shoppingList.addShoppingListItem(itemName, amount);
        shoppingListRepository.save(shoppingList);
    }

    public void removeEntry(String listName, String itemName) {
        ShoppingList shoppingList = getShoppingList(listName);
        shoppingList.removeShoppingListItem(itemName);
        shoppingListRepository.save(shoppingList);
    }

    public void buyEntry(String listName, String itemName) {
        ShoppingList shoppingList = getShoppingList(listName);
        shoppingList.updateBoughtStateOfShoppingListItem(itemName, true);
        shoppingListRepository.save(shoppingList);
    }

    public void unbuyEntry(String listName, String itemName) {
        ShoppingList shoppingList = getShoppingList(listName);
        shoppingList.updateBoughtStateOfShoppingListItem(itemName, false);
        shoppingListRepository.save(shoppingList);
    }

    public void updateAmount(String listName, String itemName, Amount amount) {
        ShoppingList shoppingList = getShoppingList(listName);
        shoppingList.updateAmountOfShoppingListItem(itemName, amount);
        shoppingListRepository.save(shoppingList);
    }

    public void clearBoughtShoppingListItems(String listName) {
        ShoppingList shoppingList = getShoppingList(listName);
        Set<ShoppingListItem> boughtItems = shoppingList.getShoppingListItems()
                                                        .stream()
                                                        .filter(ShoppingListItem::isBought)
                                                        .collect(Collectors.toSet());
        shoppingList.removeShoppingListItems(boughtItems);
        shoppingListRepository.save(shoppingList);
    }

    public void deleteShoppingList(String name) {
        shoppingListRepository.findByListName(name)
                              .ifPresentOrElse(shoppingListRepository::delete,
                                               () -> {
                                                   throw new ShoppingListNotFoundException(name);
                                               });
    }
}
