package org.example.repositories;

import org.example.aggregates.Item;
import org.example.aggregates.StoredItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoredItemRepository {
    public void save(StoredItem storedItem);

    public Optional<StoredItem> findById(UUID id);

    public Optional<StoredItem> findByReferencedItem(Item item);

    public List<StoredItem> getAll();

    public void delete(StoredItem storedItem);
}
