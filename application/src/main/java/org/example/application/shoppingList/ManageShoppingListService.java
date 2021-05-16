package org.example.application.shoppingList;

import org.example.application.exceptions.ShoppingListAlreadyExistsException;
import org.example.application.exceptions.ShoppingListNotFoundException;
import org.example.entities.aggregateRoots.ShoppingList;
import org.example.repositories.ShoppingListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManageShoppingListService {

    private final ShoppingListRepository shoppingListRepository;

    @Autowired
    public ManageShoppingListService(ShoppingListRepository shoppingListRepository) {
        this.shoppingListRepository = shoppingListRepository;
    }

    public void createShoppingList(String name) {
        shoppingListRepository.findByListName(name)
                              .ifPresent(shoppingList -> {
                                  throw new ShoppingListAlreadyExistsException(name);
                              });
        ShoppingList shoppingList = new ShoppingList(name);
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
