package org.example.application.shoppingList;

import org.example.entities.ShoppingListItem;
import org.example.entities.aggregateRoots.ShoppingList;
import org.example.repositories.ShoppingListRepository;
import org.example.valueObjects.Amount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UpdateShoppingListEntriesService {

    private final ShoppingListRepository shoppingListRepository;
    private final ReadShoppingListService readShoppingListService;

    @Autowired
    public void addEntry(String listName, String itemName, Amount amount) {
        ShoppingList shoppingList = readShoppingListService.getShoppingList(listName);
        shoppingList.addShoppingListItem(itemName, amount);
        shoppingListRepository.save(shoppingList);
    }

    public UpdateShoppingListEntriesService(ShoppingListRepository shoppingListRepository, ReadShoppingListService readShoppingListService) {
        this.shoppingListRepository = shoppingListRepository;
        this.readShoppingListService = readShoppingListService;
    }

    public void removeEntry(String listName, String itemName) {
        ShoppingList shoppingList = readShoppingListService.getShoppingList(listName);
        shoppingList.removeShoppingListItem(itemName);
        shoppingListRepository.save(shoppingList);
    }

    public void buyEntry(String listName, String itemName) {
        ShoppingList shoppingList = readShoppingListService.getShoppingList(listName);
        shoppingList.updateBoughtStateOfShoppingListItem(itemName, true);
        shoppingListRepository.save(shoppingList);
    }

    public void unbuyEntry(String listName, String itemName) {
        ShoppingList shoppingList = readShoppingListService.getShoppingList(listName);
        shoppingList.updateBoughtStateOfShoppingListItem(itemName, false);
        shoppingListRepository.save(shoppingList);
    }

    public void updateAmount(String listName, String itemName, Amount amount) {
        ShoppingList shoppingList = readShoppingListService.getShoppingList(listName);
        shoppingList.updateAmountOfShoppingListItem(itemName, amount);
        shoppingListRepository.save(shoppingList);
    }

    public void clearBoughtShoppingListItems(String listName) {
        ShoppingList shoppingList = readShoppingListService.getShoppingList(listName);
        Set<ShoppingListItem> boughtItems = shoppingList.getShoppingListItems()
                                                        .stream()
                                                        .filter(ShoppingListItem::isBought)
                                                        .collect(Collectors.toSet());
        shoppingList.removeShoppingListItems(boughtItems);
        shoppingListRepository.save(shoppingList);
    }

}
