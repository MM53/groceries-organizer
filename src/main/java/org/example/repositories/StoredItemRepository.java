package org.example.repositories;

import org.example.aggregates.StoredItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoredItemRepository {
    public void save(StoredItem storedItem);

    public Optional<StoredItem> findById(UUID id);

    public List<StoredItem> getAll();
}
