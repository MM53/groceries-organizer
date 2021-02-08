package org.example.persistence.dao;

import org.example.entities.MinimumAmount;
import org.example.persistence.dao.jpa.MinimumAmountEntityRepository;
import org.example.persistence.entities.MinimumAmountEntity;
import org.example.persistence.entities.MinimumAmountEntityConverter;
import org.example.repositories.MinimumAmountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MinimumAmountJpaRepository implements MinimumAmountRepository {

    private final MinimumAmountEntityRepository jpaRepository;

    @Autowired
    public MinimumAmountJpaRepository(MinimumAmountEntityRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(MinimumAmount minimumAmount) {
        jpaRepository.save(MinimumAmountEntityConverter.convertToEntity(minimumAmount));
    }

    @Override
    public Optional<MinimumAmount> findLocationByItemName(String itemName) {
        return jpaRepository.findById(itemName)
                            .map(MinimumAmountEntityConverter::convertFromEntity);
    }

    @Override
    public List<MinimumAmount> getAll() {
        return jpaRepository.findAll()
                            .stream()
                            .map(MinimumAmountEntityConverter::convertFromEntity)
                            .collect(Collectors.toList());
    }
}
