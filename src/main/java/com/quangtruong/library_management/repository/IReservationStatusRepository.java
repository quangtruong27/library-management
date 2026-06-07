package com.quangtruong.library_management.repository;

import com.quangtruong.library_management.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IReservationStatusRepository extends JpaRepository<ReservationStatus, Long> {
	Optional<ReservationStatus> findByName(String name);
}
