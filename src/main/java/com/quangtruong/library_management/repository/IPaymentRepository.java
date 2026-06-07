package com.quangtruong.library_management.repository;

import com.quangtruong.library_management.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface IPaymentRepository extends JpaRepository<Payment, Long> {

	@Query("SELECT p FROM Payment p WHERE " +
			"(:studentId IS NULL OR p.studentProfile.id = :studentId) AND " +
			"(:startDate IS NULL OR p.paymentDate >= :startDate) AND " +
			"(:endDate IS NULL OR p.paymentDate <= :endDate) AND " +
			"(:paymentMethod IS NULL OR p.paymentMethod = :paymentMethod)")
	Page<Payment> searchPayments(@Param("studentId") UUID studentId,
								 @Param("startDate") LocalDateTime startDate,
								 @Param("endDate") LocalDateTime endDate,
								 @Param("paymentMethod") String paymentMethod,
								 Pageable pageable);

	List<Payment> findByStudentProfileId(UUID studentId);
}
