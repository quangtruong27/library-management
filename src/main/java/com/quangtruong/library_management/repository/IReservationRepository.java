package com.quangtruong.library_management.repository;

import com.quangtruong.library_management.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface IReservationRepository extends JpaRepository<Reservation, Long> {

	Page<Reservation> findByStudentProfileId(UUID studentId, Pageable pageable);

	List<Reservation> findByStudentProfileId(UUID studentId);

	// Count reservations
	long countByStudentProfileIdAndStatusNameIn(UUID studentId, List<String> statusNames);

	List<Reservation> findByStudentProfileIdAndStatusNameIn(UUID studentId, List<String> statusNames);

	// Find expired reservations
	List<Reservation> findAllByStatusNameAndExpiredDateBefore(String statusName, LocalDateTime now);
}
