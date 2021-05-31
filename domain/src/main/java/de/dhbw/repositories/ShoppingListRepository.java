package de.dhbw.repositories;

import de.dhbw.aggregates.ShoppingList;

import java.util.List;
import java.util.Optional;

public interface ShoppingListRepository {
    public void save(ShoppingList shoppingList);

    public Optional<ShoppingList> findByListName(String listName);

    public boolean checkExistenceByListName(String listName);

    public List<ShoppingList> getAll();

    public void delete(ShoppingList shoppingList);
}
