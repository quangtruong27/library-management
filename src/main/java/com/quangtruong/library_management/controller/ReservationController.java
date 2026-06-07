package com.quangtruong.library_management.controller;

import com.quangtruong.library_management.dto.ApiResponse;
import com.quangtruong.library_management.dto.PageResponse;
import com.quangtruong.library_management.dto.borrow.BorrowResponse;
import com.quangtruong.library_management.dto.reservation.ReservationRequest;
import com.quangtruong.library_management.dto.reservation.ReservationResponse;
import com.quangtruong.library_management.dto.reservation.ReservationStatusRequest;
import com.quangtruong.library_management.service.IBorrowService;
import com.quangtruong.library_management.service.IReservationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

	IReservationService reservationService;
	IBorrowService borrowService;

	private UUID getCurrentUserId() {
		return UUID.fromString(
				SecurityContextHolder.getContext().getAuthentication().getName());
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<PageResponse<ReservationResponse>>> getAll(
			@PageableDefault(size = 5, sort = "reservationDate", direction = Sort.Direction.DESC) Pageable pageable) {

		return ResponseEntity.ok(ApiResponse.<PageResponse<ReservationResponse>>builder()
				.data(new PageResponse<>(reservationService.getAll(pageable)))
				.build());
	}

	// View details of a reservation
	@GetMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<ReservationResponse>> getById(@PathVariable Long id) {

		return ResponseEntity.ok(ApiResponse.<ReservationResponse>builder()
				.data(reservationService.getById(id))
				.build());
	}

	// Student creates reservation request
	@PostMapping
	@PreAuthorize("hasRole('STUDENT')")
	public ResponseEntity<ApiResponse<ReservationResponse>> create(@Valid @RequestBody ReservationRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.<ReservationResponse>builder()
						.data(reservationService.create(request, getCurrentUserId()))
						.build());
	}

	// Cancel reservation (Student cancels by themselves or Librarian cancels at counter)
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('STUDENT') or hasRole('STAFF') or hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<String>> cancel(@PathVariable Long id) {

		var auth = SecurityContextHolder.getContext().getAuthentication();

		boolean isStaffOrAdmin = auth.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_STAFF") || a.getAuthority().equals("ROLE_ADMIN"));

		if (isStaffOrAdmin) {
			borrowService.cancelReservationAtCounter(id);
		} else {
			reservationService.cancel(id, getCurrentUserId());
		}

		return ResponseEntity.ok(ApiResponse.<String>builder()
				.data("Hủy đặt trước thành công")
				.build());
	}

	// Librarian confirms student arriving to pick up reserved book
	@PostMapping("/{reservationId}/confirm-pickup")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<BorrowResponse>> confirmPickup(@PathVariable Long reservationId) {
		UUID staffUserId = getCurrentUserId();
		return ResponseEntity.ok(ApiResponse.<BorrowResponse>builder()
				.data(borrowService.confirmReservationPickup(reservationId, staffUserId))
				.build());
	}

	// Staff updates status
	@PatchMapping("/{id}/status")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<ReservationResponse>> updateStatus(@PathVariable Long id,
			@Valid @RequestBody ReservationStatusRequest request) {

		return ResponseEntity.ok(ApiResponse.<ReservationResponse>builder()
				.data(reservationService.updateStatus(id, request.getStatus()))
				.build());
	}
}
