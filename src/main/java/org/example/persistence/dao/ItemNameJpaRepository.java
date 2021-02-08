package org.example.persistence.dao;

import org.example.entities.Item;
import org.example.entities.ItemNameAlternative;
import org.example.persistence.dao.jpa.ItemNameAlternativeRepository;
import org.example.persistence.entities.ItemNameAlternativeEntityConverter;
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
        jpaRepository.save(ItemNameAlternativeEntityConverter.convertToEntity(alternativeName));
    }

    @Override
    public Optional<ItemNameAlternative> findAlternativeByName(String name) {
        return jpaRepository.findById(name)
                            .map(ItemNameAlternativeEntityConverter::convertFromEntity);
    }

    @Override
    public List<ItemNameAlternative> findAlternativesForItem(Item item) {
        return jpaRepository.findItemNameAlternativeEntitiesByAlternativeFor(item.getPrimaryName())
                            .stream()
                            .map(ItemNameAlternativeEntityConverter::convertFromEntity)
                            .collect(Collectors.toList());
    }

    @Override
    public List<ItemNameAlternative> getAll() {
        return jpaRepository.findAll()
                            .stream()
                            .map(ItemNameAlternativeEntityConverter::convertFromEntity)
                            .collect(Collectors.toList());
    }
}
