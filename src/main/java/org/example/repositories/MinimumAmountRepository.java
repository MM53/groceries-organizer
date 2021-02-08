package org.example.repositories;

import org.example.entities.MinimumAmount;

import java.util.List;
import java.util.Optional;

public interface MinimumAmountRepository {
    public void save(MinimumAmount minimumAmount);

    public Optional<MinimumAmount> findLocationByItemName(String itemName);

    public List<MinimumAmount> getAll();
}
