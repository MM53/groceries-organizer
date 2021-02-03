package org.example.repositories;

import org.example.entities.ItemLocation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemLocationRepository {
    public void save(ItemLocation location);

    public Optional<ItemLocation> findLocationById(UUID id);

    public List<ItemLocation> getAll();
}
