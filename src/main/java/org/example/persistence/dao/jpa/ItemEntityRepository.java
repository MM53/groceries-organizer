package org.example.persistence.dao.jpa;

import org.example.persistence.entities.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemEntityRepository extends JpaRepository<ItemEntity, String> {

}
