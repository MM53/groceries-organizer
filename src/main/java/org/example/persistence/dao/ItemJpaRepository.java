package org.example.persistence.dao;

import org.example.entities.Item;
import org.example.entities.ItemNameAlternative;
import org.example.persistence.dao.jpa.ItemEntityRepository;
import org.example.persistence.entities.ItemEntity;
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
        ItemEntity entity = new ItemEntity();
        entity.setPrimaryName(item.getPrimaryName());
        entity.setAlternativeNames(item.getAlternativeNames()
                                       .stream()
                                       .map(this::convertToEntity)
                                       .collect(Collectors.toList()));
        entity.setUnitType(item.getUnitType());
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Item> findItemByPrimaryName(String name) {
        return jpaRepository.findById(name)
                            .map(this::convertFromEntity);
    }

    @Override
    public List<Item> getAll() {
        return jpaRepository.findAll()
                            .stream()
                            .map(this::convertFromEntity)
                            .collect(Collectors.toList());
    }

    private ItemNameAlternativeEntity convertToEntity(ItemNameAlternative alternative) {
        ItemNameAlternativeEntity entity = new ItemNameAlternativeEntity();
        entity.setName(alternative.getName());
        entity.setAlternativeFor(alternative.getAlternativeFor());
        return entity;
    }

    private Item convertFromEntity(ItemEntity entity) {
        return new Item(entity.getPrimaryName(),
                        entity.getAlternativeNames()
                              .stream()
                              .map(nameAlternativeEntity -> new ItemNameAlternative(nameAlternativeEntity.getName(),
                                                                                    nameAlternativeEntity.getAlternativeFor()))
                              .collect(Collectors.toList()),
                        entity.getUnitType());
    }
}
