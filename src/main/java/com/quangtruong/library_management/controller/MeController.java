package com.quangtruong.library_management.controller;

import com.quangtruong.library_management.dto.ApiResponse;
import com.quangtruong.library_management.dto.PageResponse;
import com.quangtruong.library_management.dto.borrow.BorrowResponse;
import com.quangtruong.library_management.dto.fine.FineResponse;
import com.quangtruong.library_management.dto.reservation.ReservationResponse;
import com.quangtruong.library_management.dto.student.StudentProfileResponse;
import com.quangtruong.library_management.mapper.IBorrowMapper;
import com.quangtruong.library_management.mapper.IFineMapper;
import com.quangtruong.library_management.repository.IBorrowRepository;
import com.quangtruong.library_management.repository.IFineRepository;
import com.quangtruong.library_management.service.IReservationService;
import com.quangtruong.library_management.service.IStudentProfileService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/me")
public class MeController {

	IStudentProfileService studentProfileService;
	IReservationService reservationService;
	IBorrowRepository borrowRepository;
	IFineRepository fineRepository;
	IBorrowMapper borrowMapper;
	IFineMapper fineMapper;

	// Automatically extract user ID from token
	private UUID getCurrentUserId() {
		String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
		return UUID.fromString(userIdStr);
	}

	// 1. View personal profile
	@GetMapping("/profile")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<StudentProfileResponse>> getMyProfile() {
		UUID userId = getCurrentUserId();
		return ResponseEntity.ok(ApiResponse.<StudentProfileResponse>builder()
				.data(studentProfileService.getById(userId))
				.build());
	}

	// 2. View currently borrowed books
	@GetMapping("/borrowed-items")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<List<BorrowResponse>>> getMyBorrowedItems() {
		UUID userId = getCurrentUserId();
		List<BorrowResponse> activeBorrows = borrowRepository
				.findByStudentProfileIdAndStatus(userId, "BORROWING").stream()
				.map(borrowMapper::toResponse)
				.collect(Collectors.toList());

		return ResponseEntity.ok(ApiResponse.<List<BorrowResponse>>builder()
				.data(activeBorrows)
				.build());
	}

	// 3. View borrow and return history
	@GetMapping("/borrow-history")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<List<BorrowResponse>>> getMyBorrowHistory() {
		UUID userId = getCurrentUserId();
		List<BorrowResponse> history = borrowRepository
				.findByStudentProfileId(userId).stream()
				.map(borrowMapper::toResponse)
				.collect(Collectors.toList());

		return ResponseEntity.ok(ApiResponse.<List<BorrowResponse>>builder()
				.data(history)
				.build());
	}

	// 4. View current reservations
	@GetMapping("/reservations")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<PageResponse<ReservationResponse>>> getMyReservations(
			@PageableDefault(size = 5, sort = "reservationDate",
					direction = Sort.Direction.DESC) Pageable pageable) {

		UUID userId = getCurrentUserId();
		return ResponseEntity.ok(ApiResponse.<PageResponse<ReservationResponse>>builder()
				.data(new PageResponse<>(reservationService.getMyReservations(userId, pageable)))
				.build());
	}

	// 5. View violations & outstanding fines
	@GetMapping("/violations")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<List<FineResponse>>> getMyViolations() {
		UUID userId = getCurrentUserId();
		List<FineResponse> violations = fineRepository
				.findByStudentProfileId(userId).stream()
				.map(fineMapper::toResponse)
				.collect(Collectors.toList());

		return ResponseEntity.ok(ApiResponse.<List<FineResponse>>builder()
				.data(violations)
				.build());
	}

	// 6. View total fine to be paid
	@GetMapping("/fines")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<BigDecimal>> getMyFines() {
		UUID userId = getCurrentUserId();
		BigDecimal totalFines = fineRepository
				.findByStudentProfileIdAndStatus(userId, "UNPAID").stream()
				.map(f -> f.getReferencePrice() != null ? f.getReferencePrice() : BigDecimal.ZERO)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		return ResponseEntity.ok(ApiResponse.<BigDecimal>builder()
				.data(totalFines)
				.build());
	}

	// 7. View payment history
	@GetMapping("/fine-payments")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<String>> getMyFinePayments() {
		// Pending Payment Module
		return ResponseEntity.ok(ApiResponse.<String>builder().data("Đang phát triển").build());
	}

	// 8. Receive notifications
	@GetMapping("/notifications")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<String>> getMyNotifications() {
		// Pending Notification Module
		return ResponseEntity.ok(ApiResponse.<String>builder().data("Đang phát triển").build());
	}
}
