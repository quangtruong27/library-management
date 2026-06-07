package com.quangtruong.library_management.repository;

import com.quangtruong.library_management.entity.BookReturn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface IBookReturnRepository extends JpaRepository<BookReturn, Long> {

	@Query("SELECT r FROM BookReturn r WHERE " +
			"(:studentId IS NULL OR r.borrowDetail.borrow.studentProfile.id = :studentId) AND " +
			"(:status IS NULL OR r.status = :status) AND " +
			"(:startDate IS NULL OR r.returnDate >= :startDate) AND " +
			"(:endDate IS NULL OR r.returnDate <= :endDate) AND " +
			"(:hasViolation IS NULL OR " +
			"  (:hasViolation = true AND (r.fineAmount > 0 OR r.bookCondition <> 'NORMAL')) OR " +
			"  (:hasViolation = false AND (r.fineAmount IS NULL OR r.fineAmount = 0) AND r.bookCondition = 'NORMAL'))")
	Page<BookReturn> searchReturns(@Param("studentId") UUID studentId,
								   @Param("status") String status,
								   @Param("startDate") LocalDateTime startDate,
								   @Param("endDate") LocalDateTime endDate,
								   @Param("hasViolation") Boolean hasViolation,
								   Pageable pageable);
	java.util.Optional<BookReturn> findByBorrowDetailId(Long borrowDetailId);

	java.util.Optional<BookReturn> findByBorrowDetailBorrowId(Long borrowId);
}
