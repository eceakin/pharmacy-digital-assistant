package com.pharmacy.assistant.infrastructure.adapter.persistence.jpa;

import com.pharmacy.assistant.domain.enums.PrescriptionStatus;
import com.pharmacy.assistant.infrastructure.adapter.persistence.entity.PrescriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface PrescriptionJpaRepository extends JpaRepository<PrescriptionEntity, UUID> {

    Optional<PrescriptionEntity> findByPrescriptionNumber(String prescriptionNumber);

    List<PrescriptionEntity> findByPatientId(UUID patientId);

    List<PrescriptionEntity> findByStatus(PrescriptionStatus status);

    List<PrescriptionEntity> findByPatientIdAndStatus(UUID patientId, PrescriptionStatus status);

    List<PrescriptionEntity> findByEndDateBetween(LocalDate startDate, LocalDate endDate);

    // ✅ İyileştirilmiş: Enum parametresi kullanımı
    @Query("SELECT p FROM PrescriptionEntity p WHERE " +
            "p.status = :status AND p.endDate BETWEEN :today AND :thresholdDate")
    List<PrescriptionEntity> findExpiringSoon(
            @Param("status") PrescriptionStatus status,
            @Param("today") LocalDate today,
            @Param("thresholdDate") LocalDate thresholdDate
    );

    // ✅ İyileştirilmiş: Hem süresi geçmiş aktifler hem EXPIRED statüsündekiler
    @Query("SELECT p FROM PrescriptionEntity p WHERE " +
            "p.endDate < :today OR p.status = :expiredStatus")
    List<PrescriptionEntity> findExpiredPrescriptions(
            @Param("today") LocalDate today,
            @Param("expiredStatus") PrescriptionStatus expiredStatus
    );

    // ✅ Yeni: Refill hakkı kalanlar
    @Query("SELECT p FROM PrescriptionEntity p WHERE " +
            "p.status = :status AND p.refillsRemaining > 0")
    List<PrescriptionEntity> findWithRefillsRemaining(
            @Param("status") PrescriptionStatus status
    );
    @Query("SELECT p FROM PrescriptionEntity p WHERE p.status = 'ACTIVE' AND p.endDate BETWEEN :startDate AND :endDate")
    List<PrescriptionEntity> findActiveExpiringBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    boolean existsByPrescriptionNumber(String prescriptionNumber);

    long countByPatientId(UUID patientId);

    long countByStatus(PrescriptionStatus status);

    // ✅ Yeni: Belirli tarihte süresi dolacak reçete sayısı
    @Query("SELECT COUNT(p) FROM PrescriptionEntity p WHERE " +
            "p.status = :status AND p.endDate = :date")
    long countExpiringOnDate(
            @Param("status") PrescriptionStatus status,
            @Param("date") LocalDate date
    );
}