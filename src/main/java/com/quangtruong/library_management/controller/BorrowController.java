package com.quangtruong.library_management.controller;

import com.quangtruong.library_management.dto.ApiResponse;
import com.quangtruong.library_management.dto.PageResponse;
import com.quangtruong.library_management.dto.borrow.*;
import com.quangtruong.library_management.service.IBorrowService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api")
public class BorrowController {

	IBorrowService borrowService;

	private UUID getCurrentUserId() {

		return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
	}

	// 1. Scan student card: Get current borrow information
	@GetMapping("/students/{studentId}/borrow-session")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<BorrowSessionResponse>> getBorrowSession(@PathVariable UUID studentId) {

		return ResponseEntity.ok(ApiResponse.<BorrowSessionResponse>builder()
				.data(borrowService.getBorrowSession(studentId))
				.build());
	}

	// 2. Check free-borrow conditions
	@PostMapping("/loans/check-eligibility")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<CheckEligibilityResponse>> checkEligibility(
			@Valid @RequestBody CheckEligibilityRequest request) {

		return ResponseEntity.ok(ApiResponse.<CheckEligibilityResponse>builder()
				.data(borrowService.checkEligibility(request))
				.build());
	}

	// 3. Create borrow record
	@PostMapping("/loans")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<BorrowResponse>> createBorrow(@Valid @RequestBody BorrowRequest request) {

		UUID staffUserId = getCurrentUserId();

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.<BorrowResponse>builder()
						.data(borrowService.createBorrow(request, staffUserId))
						.build());
	}

	// 4. Confirm book delivered (confirm pickup)
	@PostMapping("/loans/{loanId}/confirm-pickup")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<BorrowResponse>> confirmLoanPickup(@PathVariable Long loanId) {

		return ResponseEntity.ok(ApiResponse.<BorrowResponse>builder()
				.data(borrowService.confirmLoanPickup(loanId))
				.build());
	}

	// 5. Get borrow records list for monitoring
	@GetMapping("/loans")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<PageResponse<BorrowResponse>>> getAll(
			@RequestParam(required = false) UUID studentId,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
			@PageableDefault(size = 5, sort = "borrowDate", direction = Sort.Direction.DESC) Pageable pageable) {

		return ResponseEntity.ok(ApiResponse.<PageResponse<BorrowResponse>>builder()
				.data(new PageResponse<>(borrowService.getAllBorrows(studentId, status, startDate, endDate, pageable)))
				.build());
	}

	// 6. View borrow record details
	@GetMapping("/loans/{loanId}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF') or hasRole('STUDENT')")
	public ResponseEntity<ApiResponse<BorrowResponse>> getById(@PathVariable Long loanId) {

		return ResponseEntity.ok(ApiResponse.<BorrowResponse>builder()
				.data(borrowService.getBorrowById(loanId))
				.build());
	}

	// 7. Cancel/Remove borrow record before delivery
	@DeleteMapping("/loans/{loanId}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<String>> deleteBorrow(@PathVariable Long loanId) {

		borrowService.deleteBorrow(loanId);

		return ResponseEntity.ok(ApiResponse.<String>builder()
				.data("Hủy phiếu mượn thành công")
				.build());
	}

	// 8. Mark as lost/damaged during borrowing
	@PatchMapping("/loans/{loanId}/mark-issue")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<BorrowResponse>> markIssue(@PathVariable Long loanId,
																 @RequestParam String issueType) {

		return ResponseEntity.ok(ApiResponse.<BorrowResponse>builder()
				.data(borrowService.markIssue(loanId, issueType))
				.build());
	}
}
