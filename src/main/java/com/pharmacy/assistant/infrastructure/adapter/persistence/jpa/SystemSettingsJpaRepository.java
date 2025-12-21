package com.pharmacy.assistant.infrastructure.adapter.persistence.jpa;

import com.pharmacy.assistant.infrastructure.adapter.persistence.entity.SystemSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SystemSettingsJpaRepository extends JpaRepository<SystemSettingsEntity, UUID> {

    /**
     * Find the first (and should be only) settings record
     * System should have only ONE settings record
     */
    Optional<SystemSettingsEntity> findFirstByOrderByCreatedAtAsc();
}
