package org.example.application.shoppingList;

import org.example.aggregates.ShoppingList;
import org.example.application.exceptions.RemoveDefaultShoppingListException;
import org.example.application.exceptions.ShoppingListAlreadyExistsException;
import org.example.application.exceptions.ShoppingListNotFoundException;
import org.example.repositories.ShoppingListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManageShoppingListService {

    private final ShoppingListRepository shoppingListRepository;

    @Autowired
    public ManageShoppingListService(ShoppingListRepository shoppingListRepository) {
        this.shoppingListRepository = shoppingListRepository;
        createDefaultShoppingList();
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
        if (name.equals(ShoppingList.DEFAULT_SHOPPING_LIST)) {
            throw new RemoveDefaultShoppingListException();
        }
        shoppingListRepository.findByListName(name)
                              .ifPresentOrElse(shoppingListRepository::delete,
                                               () -> {
                                                   throw new ShoppingListNotFoundException(name);
                                               });
    }

    private void createDefaultShoppingList() {
        if (!shoppingListRepository.checkExistenceByListName(ShoppingList.DEFAULT_SHOPPING_LIST)) {
            shoppingListRepository.save(new ShoppingList(ShoppingList.DEFAULT_SHOPPING_LIST));
        }
    }

}
