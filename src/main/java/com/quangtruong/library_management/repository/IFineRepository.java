package com.quangtruong.library_management.repository;

import com.quangtruong.library_management.entity.Fine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IFineRepository extends JpaRepository<Fine, Long> {
	List<Fine> findByStudentProfileId(UUID studentId);
	List<Fine> findByStudentProfileIdAndStatus(UUID studentId, String status);
	boolean existsByStudentProfileIdAndStatus(UUID studentId, String status);

	@Query("SELECT f FROM Fine f WHERE " +
			"(:studentId IS NULL OR f.studentProfile.id = :studentId) AND " +
			"(:status IS NULL OR f.status = :status) AND " +
			"(:fineType IS NULL OR f.fineType = :fineType)")
	Page<Fine> searchFines(@Param("studentId") UUID studentId,
						   @Param("status") String status,
						   @Param("fineType") String fineType,
						   Pageable pageable);
}
