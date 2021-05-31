package de.dhbw.repositories;

import de.dhbw.aggregates.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    public void save(Item item);

    public Optional<Item> findItemById(String id);

    public boolean checkExistenceById(String id);

    public Optional<Item> findItemByName(String name);

    public List<Item> getAll();

    public void delete(Item item);
}
