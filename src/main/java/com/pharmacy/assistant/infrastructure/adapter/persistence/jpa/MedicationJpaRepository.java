package com.pharmacy.assistant.infrastructure.adapter.persistence.jpa;

import com.pharmacy.assistant.domain.enums.MedicationStatus;
import com.pharmacy.assistant.infrastructure.adapter.persistence.entity.MedicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface MedicationJpaRepository extends JpaRepository<MedicationEntity, UUID> {

    List<MedicationEntity> findByPatientId(UUID patientId);
    List<MedicationEntity> findByProductId(UUID productId);
    List<MedicationEntity> findByStatus(MedicationStatus status);
    List<MedicationEntity> findByPatientIdAndStatus(UUID patientId, MedicationStatus status);

    // ❌ ESKİ - SİLİN VEYA YORUM SATIRI YAPIN
    // List<MedicationEntity> findByScheduleEndDateBetween(LocalDate startDate, LocalDate endDate);
// Sadece AKTİF olan ve süresi yaklaşanları getir
    List<MedicationEntity> findByStatusAndScheduleEndDateBetween(MedicationStatus status, LocalDate startDate, LocalDate endDate);
    // ✅ YENİ - DAHA DOĞRU SORGU
    @Query("SELECT m FROM MedicationEntity m WHERE " +
            "m.status = 'ACTIVE' AND " +
            "m.scheduleEndDate IS NOT NULL AND " +
            "m.scheduleEndDate >= :today AND " +
            "m.scheduleEndDate <= :thresholdDate")
    List<MedicationEntity> findExpiringBetween(
            @Param("today") LocalDate today,
            @Param("thresholdDate") LocalDate thresholdDate
    );

    // ✅ YENİ - Refill gerekenler (7 gün içinde)
    @Query("SELECT m FROM MedicationEntity m WHERE " +
            "m.status = 'ACTIVE' AND " +
            "m.scheduleEndDate IS NOT NULL AND " +
            "m.scheduleEndDate >= :today AND " +
            "m.scheduleEndDate <= :thresholdDate")
    List<MedicationEntity> findNeedingRefill(
            @Param("today") LocalDate today,
            @Param("thresholdDate") LocalDate thresholdDate
    );

    long countByPatientId(UUID patientId);
    long countByStatus(MedicationStatus status);
}