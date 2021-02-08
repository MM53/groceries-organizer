package org.example.persistence.dao.jpa;

import org.example.persistence.entities.StoredItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StoredItemEntityRepository extends JpaRepository<StoredItemEntity, UUID> {
}
