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

    // Aktif reçeteler
    List<PrescriptionEntity> findByPatientIdAndStatus(UUID patientId, PrescriptionStatus status);

    // Belirli tarihler arasında bitiş tarihi olanlar
    List<PrescriptionEntity> findByEndDateBetween(LocalDate startDate, LocalDate endDate);

    // Süresi yaklaşanlar: Durumu ACTIVE olan VE Bitiş tarihi bugün ile eşik tarih arasında olanlar
    @Query("SELECT p FROM PrescriptionEntity p WHERE p.status = 'ACTIVE' AND p.endDate BETWEEN :today AND :thresholdDate")
    List<PrescriptionEntity> findExpiringSoon(@Param("today") LocalDate today, @Param("thresholdDate") LocalDate thresholdDate);

    // Süresi dolmuşlar: Bitiş tarihi bugünden önce olanlar (veya statüsü EXPIRED olanlar)
    @Query("SELECT p FROM PrescriptionEntity p WHERE p.endDate < :today AND p.status = 'ACTIVE'")
    List<PrescriptionEntity> findExpiredPrescriptions(@Param("today") LocalDate today);

    boolean existsByPrescriptionNumber(String prescriptionNumber);

    long countByPatientId(UUID patientId);

    long countByStatus(PrescriptionStatus status);
}