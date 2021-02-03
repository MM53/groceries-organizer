package org.example.persistence.dao.jpa;

import org.example.persistence.entities.ItemLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemLocationEntityRepository extends JpaRepository<ItemLocationEntity, UUID> {
}
