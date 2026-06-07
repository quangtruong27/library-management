package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.reservation.ReservationRequest;
import com.quangtruong.library_management.dto.reservation.ReservationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IReservationService {
	Page<ReservationResponse> getAll(Pageable pageable);
	Page<ReservationResponse> getMyReservations(UUID userId, Pageable pageable);
	ReservationResponse getById(Long id);
	ReservationResponse create(ReservationRequest request, UUID userId);
	void cancel(Long id, UUID userId);
	ReservationResponse updateStatus(Long id, String statusName);
	void processExpiredReservations();
}
