package com.quangtruong.library_management.controller;

import com.quangtruong.library_management.dto.ApiResponse;
import com.quangtruong.library_management.dto.PageResponse;
import com.quangtruong.library_management.dto.fine.*;
import com.quangtruong.library_management.service.IFineService;
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
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api")
public class FineController {

	IFineService fineService;

	private UUID getCurrentUserId() {
		return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
	}

	// 1. List of fines
	@GetMapping("/fines")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<PageResponse<FineDetailResponse>>> getAllFines(
			@RequestParam(required = false) UUID studentId,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) String fineType,
			@PageableDefault(size = 5, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

		return ResponseEntity.ok(ApiResponse.<PageResponse<FineDetailResponse>>builder()
				.data(new PageResponse<>(fineService.getAllFines(studentId, status, fineType, pageable)))
				.build());
	}

	// 2. Get fine details
	@GetMapping("/fines/{fineId}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF') or hasRole('STUDENT')")
	public ResponseEntity<ApiResponse<FineDetailResponse>> getFineById(@PathVariable Long fineId) {
		return ResponseEntity.ok(ApiResponse.<FineDetailResponse>builder()
				.data(fineService.getFineById(fineId))
				.build());
	}

	// 3. Create fine manually
	@PostMapping("/fines")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<FineDetailResponse>> createFineManual(
			@Valid @RequestBody FineCreateRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.<FineDetailResponse>builder()
						.data(fineService.createFineManual(request))
						.build());
	}

	// 4. Update fine
	@PutMapping("/fines/{fineId}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<FineDetailResponse>> updateFine(
			@PathVariable Long fineId,
			@Valid @RequestBody FineUpdateRequest request) {

		return ResponseEntity.ok(ApiResponse.<FineDetailResponse>builder()
				.data(fineService.updateFine(fineId, request))
				.build());
	}

	// 5. Record payment
	@PostMapping("/fines/{fineId}/payments")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<PaymentResponse>> recordPayment(
			@PathVariable Long fineId,
			@Valid @RequestBody PaymentRequest request) {

		UUID staffUserId = getCurrentUserId();
		return ResponseEntity.ok(ApiResponse.<PaymentResponse>builder()
				.data(fineService.recordPayment(fineId, request, staffUserId))
				.build());
	}

	// 6. List of fine payments
	@GetMapping("/fine-payments")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<PageResponse<PaymentResponse>>> getAllPayments(
			@RequestParam(required = false) UUID studentId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
			@RequestParam(required = false) String paymentMethod,
			@PageableDefault(size = 5, sort = "paymentDate", direction = Sort.Direction.DESC) Pageable pageable) {

		return ResponseEntity.ok(ApiResponse.<PageResponse<PaymentResponse>>builder()
				.data(new PageResponse<>(fineService.getAllPayments(studentId, startDate, endDate, paymentMethod, pageable)))
				.build());
	}

	// 7. View payment history for a fine
	@GetMapping("/fines/{fineId}/payments")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF') or hasRole('STUDENT')")
	public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentHistoryForFine(@PathVariable Long fineId) {
		return ResponseEntity.ok(ApiResponse.<List<PaymentResponse>>builder()
				.data(fineService.getPaymentHistoryForFine(fineId))
				.build());
	}

	// 8. Cancel/Delete fine
	@DeleteMapping("/fines/{fineId}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<String>> deleteFine(@PathVariable Long fineId) {
		fineService.deleteFine(fineId);
		return ResponseEntity.ok(ApiResponse.<String>builder()
				.data("Hủy khoản phạt thành công")
				.build());
	}

	// 9. Send payment receipt
	@PostMapping("/fines/{fineId}/send-receipt")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<String>> sendPaymentReceipt(@PathVariable Long fineId) {
		fineService.sendPaymentReceipt(fineId);
		return ResponseEntity.ok(ApiResponse.<String>builder()
				.data("Gửi biên nhận thanh toán thành công qua email cho sinh viên")
				.build());
	}

	// 10. Get fine balance of student
	@GetMapping("/students/{studentId}/fine-balance")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF') or (isAuthenticated() and principal.claims['sub'] == #studentId.toString())")
	public ResponseEntity<ApiResponse<FineBalanceResponse>> getFineBalance(@PathVariable UUID studentId) {
		return ResponseEntity.ok(ApiResponse.<FineBalanceResponse>builder()
				.data(fineService.getFineBalance(studentId))
				.build());
	}
}
