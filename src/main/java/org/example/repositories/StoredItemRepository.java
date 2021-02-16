package org.example.repositories;

import org.example.entities.aggregateRoots.StoredItem;
import org.example.entities.aggregateRoots.Item;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoredItemRepository {
    public void save(StoredItem storedItem);

    public Optional<StoredItem> findById(UUID id);

    public Optional<StoredItem> findByReferencedItem(Item item);

    public List<StoredItem> getAll();
}
