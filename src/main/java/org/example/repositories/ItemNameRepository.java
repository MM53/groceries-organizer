package org.example.repositories;

import org.example.entities.Item;
import org.example.entities.ItemNameAlternative;

import java.util.List;
import java.util.Optional;

public interface ItemNameRepository {
    public void save(ItemNameAlternative alternativeName);

    public Optional<ItemNameAlternative> findAlternativeByName(String name);

    public List<ItemNameAlternative> findAlternativesForItem(Item item);

    public List<ItemNameAlternative> getAll();
}
