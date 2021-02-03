package org.example.persistence.dao;

import org.example.entities.Item;
import org.example.entities.ItemNameAlternative;
import org.example.persistence.dao.jpa.ItemNameAlternativeRepository;
import org.example.persistence.entities.ItemNameAlternativeEntity;
import org.example.repositories.ItemNameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ItemNameJpaRepository implements ItemNameRepository {

    private final ItemNameAlternativeRepository jpaRepository;

    @Autowired
    public ItemNameJpaRepository(ItemNameAlternativeRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(ItemNameAlternative alternativeName) {
        ItemNameAlternativeEntity entity = new ItemNameAlternativeEntity();
        entity.setName(alternativeName.getName());
        entity.setAlternativeFor(alternativeName.getAlternativeFor());
        jpaRepository.save(entity);
    }

    @Override
    public Optional<ItemNameAlternative> findAlternativeByName(String name) {
        return jpaRepository.findById(name)
                            .map(this::convertFromEntity);
    }

    @Override
    public List<ItemNameAlternative> findAlternativesForItem(Item item) {
        return jpaRepository.findItemNameAlternativeEntitiesByAlternativeFor(item.getPrimaryName())
                            .stream()
                            .map(this::convertFromEntity)
                            .collect(Collectors.toList());
    }

    @Override
    public List<ItemNameAlternative> getAll() {
        return jpaRepository.findAll()
                            .stream()
                            .map(this::convertFromEntity)
                            .collect(Collectors.toList());
    }

    private ItemNameAlternative convertFromEntity(ItemNameAlternativeEntity entity) {
        return new ItemNameAlternative(entity.getName(),
                                       entity.getAlternativeFor());
    }
}
