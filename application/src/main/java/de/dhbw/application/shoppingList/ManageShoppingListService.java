package de.dhbw.application.shoppingList;

import de.dhbw.aggregates.ShoppingList;
import de.dhbw.application.exceptions.RemoveDefaultShoppingListException;
import de.dhbw.application.exceptions.ShoppingListAlreadyExistsException;
import de.dhbw.application.exceptions.ShoppingListNotFoundException;
import de.dhbw.repositories.ShoppingListRepository;
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
