package org.example.repositories;

import org.example.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    public void save(Item item);

    public Optional<Item> findItemByPrimaryName(String name);

    public List<Item> getAll();
}
