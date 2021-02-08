package org.example.persistence.dao;

import org.example.entities.ItemLocation;
import org.example.persistence.dao.jpa.ItemLocationEntityRepository;
import org.example.persistence.entities.ItemLocationEntity;
import org.example.persistence.entities.ItemLocationEntityConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ItemLocationJpaRepository implements org.example.repositories.ItemLocationRepository {

    private final ItemLocationEntityRepository jpaRepository;

    @Autowired
    public ItemLocationJpaRepository(ItemLocationEntityRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(ItemLocation itemLocation) {
        jpaRepository.save(ItemLocationEntityConverter.convertToEntity(itemLocation));
    }

    @Override
    public Optional<ItemLocation> findLocationById(UUID id) {
        return jpaRepository.findById(id)
                            .map(ItemLocationEntityConverter::convertFromEntity);
    }

    @Override
    public List<ItemLocation> getAll() {
        return jpaRepository.findAll()
                            .stream()
                            .map(ItemLocationEntityConverter::convertFromEntity)
                            .collect(Collectors.toList());
    }
}
