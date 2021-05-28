package org.example.application.shoppingList;

import org.example.application.exceptions.ShoppingListNotFoundException;
import org.example.entities.ShoppingListItem;
import org.example.entities.aggregateRoots.ShoppingList;
import org.example.repositories.ShoppingListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReadShoppingListService {

    private final ShoppingListRepository shoppingListRepository;

    @Autowired
    public ReadShoppingListService(ShoppingListRepository shoppingListRepository) {
        this.shoppingListRepository = shoppingListRepository;
    }

    public ShoppingList getShoppingList(String listName) {
        return shoppingListRepository.findByListName(listName)
                                     .orElseThrow(() -> new ShoppingListNotFoundException(listName));
    }

    public List<ShoppingListItem> listEntries(String listName) {
        return getShoppingList(listName).getShoppingListItems()
                                        .stream()
                                        .toList();
    }

    public List<String> getShoppingListNames() {
        return shoppingListRepository.getAll()
                                     .stream()
                                     .map(ShoppingList::getName)
                                     .toList();
    }
}
