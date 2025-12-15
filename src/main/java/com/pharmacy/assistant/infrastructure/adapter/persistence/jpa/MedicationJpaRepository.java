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

    // "Active" ilaçları getirmek için (Genellikle Status kontrolü yeterlidir)
    List<MedicationEntity> findByPatientIdAndStatus(UUID patientId, MedicationStatus status);

    // Belirli bir tarih aralığında süresi dolacak ilaçlar (scheduleEndDate'e göre)
    List<MedicationEntity> findByScheduleEndDateBetween(LocalDate startDate, LocalDate endDate);

    // Refill (Yenileme) gerekenler: Aktif olan VE bitiş tarihine belirtilen günden az kalmış (ama geçmemiş) ilaçlar
    @Query("SELECT m FROM MedicationEntity m WHERE m.status = 'ACTIVE' AND m.scheduleEndDate BETWEEN :today AND :thresholdDate")
    List<MedicationEntity> findNeedingRefill(@Param("today") LocalDate today, @Param("thresholdDate") LocalDate thresholdDate);

    long countByPatientId(UUID patientId);

    long countByStatus(MedicationStatus status);
}