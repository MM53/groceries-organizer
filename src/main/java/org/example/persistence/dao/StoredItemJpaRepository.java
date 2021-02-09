package org.example.persistence.dao;

import org.example.aggregates.StoredItem;
import org.example.entities.Item;
import org.example.entities.ItemLocation;
import org.example.entities.MinimumAmount;
import org.example.persistence.dao.jpa.ItemLocationEntityRepository;
import org.example.persistence.dao.jpa.MinimumAmountEntityRepository;
import org.example.persistence.dao.jpa.StoredItemEntityRepository;
import org.example.persistence.entities.*;
import org.example.repositories.StoredItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class StoredItemJpaRepository implements StoredItemRepository {

    private final StoredItemEntityRepository storedItemEntityRepository;
    private final ItemLocationEntityRepository itemLocationEntityRepository;
    private final MinimumAmountEntityRepository minimumAmountEntityRepository;

    @Autowired
    public StoredItemJpaRepository(StoredItemEntityRepository storedItemEntityRepository,
                                   ItemLocationEntityRepository itemLocationEntityRepository,
                                   MinimumAmountEntityRepository minimumAmountEntityRepository) {
        this.storedItemEntityRepository = storedItemEntityRepository;
        this.itemLocationEntityRepository = itemLocationEntityRepository;
        this.minimumAmountEntityRepository = minimumAmountEntityRepository;
    }

    @Override
    public void save(StoredItem storedItem) {
        storedItem.getItemLocations().forEach(this::saveItemLocation);
        saveMinimumAmount(storedItem.getMinimumAmount());

        storedItemEntityRepository.save(StoredItemEntityConverter.convertToEntity(storedItem));
    }

    private void saveItemLocation(ItemLocation itemLocation) {
        itemLocationEntityRepository.save(ItemLocationEntityConverter.convertToEntity(itemLocation));
    }

    private void saveMinimumAmount(MinimumAmount minimumAmount) {
        minimumAmountEntityRepository.save(MinimumAmountEntityConverter.convertToEntity(minimumAmount));
    }

    @Override
    public Optional<StoredItem> findById(UUID id) {
        return storedItemEntityRepository.findById(id)
                                         .map(StoredItemEntityConverter::convertFromEntity);
    }

    @Override
    public Optional<StoredItem> findByReferencedItem(Item item) {
        return storedItemEntityRepository.findByItem_PrimaryName(item.getPrimaryName())
                                         .map(StoredItemEntityConverter::convertFromEntity);
    }

    @Override
    public List<StoredItem> getAll() {
        return storedItemEntityRepository.findAll()
                                         .stream()
                                         .map(StoredItemEntityConverter::convertFromEntity)
                                         .collect(Collectors.toList());
    }
}
