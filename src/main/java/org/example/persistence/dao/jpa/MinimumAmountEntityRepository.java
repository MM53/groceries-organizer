package org.example.persistence.dao.jpa;

import org.example.persistence.entities.MinimumAmountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MinimumAmountEntityRepository extends JpaRepository<MinimumAmountEntity, String> {
}
