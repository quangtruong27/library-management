package com.quangtruong.library_management.repository;

import com.quangtruong.library_management.entity.Borrow;
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
public interface IBorrowRepository extends JpaRepository<Borrow, Long> {

	@Query("SELECT b FROM Borrow b WHERE " +
			"(:studentId IS NULL OR b.studentProfile.id = :studentId) AND " +
			"(:status IS NULL OR b.status = :status) AND " +
			"(:startDate IS NULL OR b.borrowDate >= :startDate) AND " +
			"(:endDate IS NULL OR b.borrowDate <= :endDate)")
	Page<Borrow> searchBorrows(@Param("studentId") UUID studentId,
							   @Param("status") String status,
							   @Param("startDate") LocalDateTime startDate,
							   @Param("endDate") LocalDateTime endDate,
							   Pageable pageable);

	long countByStudentProfileIdAndStatusIn(UUID studentId, List<String> statuses);

	List<Borrow> findByStudentProfileId(UUID studentId);

	List<Borrow> findByStudentProfileIdAndStatus(UUID studentId, String status);
}
