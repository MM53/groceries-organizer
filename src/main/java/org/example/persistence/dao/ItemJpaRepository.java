package org.example.persistence.dao;

import org.example.entities.Item;
import org.example.entities.ItemNameAlternative;
import org.example.persistence.dao.jpa.ItemEntityRepository;
import org.example.persistence.entities.ItemEntity;
import org.example.persistence.entities.ItemEntityConverter;
import org.example.persistence.entities.ItemNameAlternativeEntity;
import org.example.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ItemJpaRepository implements ItemRepository {

    private final ItemEntityRepository jpaRepository;

    @Autowired
    public ItemJpaRepository(ItemEntityRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }


    @Override
    public void save(Item item) {
        jpaRepository.save(ItemEntityConverter.convertToEntity(item));
    }

    @Override
    public Optional<Item> findItemByPrimaryName(String name) {
        return jpaRepository.findById(name)
                            .map(ItemEntityConverter::convertFromEntity);
    }

    @Override
    public List<Item> getAll() {
        return jpaRepository.findAll()
                            .stream()
                            .map(ItemEntityConverter::convertFromEntity)
                            .collect(Collectors.toList());
    }
}
