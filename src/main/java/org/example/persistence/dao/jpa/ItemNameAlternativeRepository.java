package org.example.persistence.dao.jpa;

import org.example.persistence.entities.ItemNameAlternativeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemNameAlternativeRepository extends JpaRepository<ItemNameAlternativeEntity, String> {

    List<ItemNameAlternativeEntity> findItemNameAlternativeEntitiesByAlternativeFor(String item);
}
