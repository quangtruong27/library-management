package com.quangtruong.library_management.controller;

import com.quangtruong.library_management.dto.ApiResponse;
import com.quangtruong.library_management.dto.PageResponse;
import com.quangtruong.library_management.dto.returns.*;
import com.quangtruong.library_management.service.IReturnService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
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
public class ReturnController {

	IReturnService returnService;

	private UUID getCurrentUserId() {
		return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
	}

	// 1. Scan return book
	@PostMapping("/returns/scan")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<ReturnScanResponse>> scanReturn(@Valid @RequestBody ReturnScanRequest request) {
		return ResponseEntity.ok(ApiResponse.<ReturnScanResponse>builder()
				.data(returnService.scanReturn(request))
				.build());
	}

	// 2. Confirm return book
	@PostMapping("/returns")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<BookReturnResponse>> confirmReturn(@Valid @RequestBody ReturnRequest request) {
		UUID staffUserId = getCurrentUserId();
		return ResponseEntity.ok(ApiResponse.<BookReturnResponse>builder()
				.data(returnService.confirmReturn(request, staffUserId))
				.build());
	}

	// 3. Assess book condition
	@PostMapping("/returns/{loanId}/condition-check")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<ConditionCheckResponse>> checkCondition(
			@PathVariable Long loanId,
			@Valid @RequestBody ConditionCheckRequest request) {
		return ResponseEntity.ok(ApiResponse.<ConditionCheckResponse>builder()
				.data(returnService.checkCondition(loanId, request))
				.build());
	}

	// 4. Create damage/loss fee
	@PostMapping("/returns/{loanId}/issues")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<BookReturnResponse>> createIssue(
			@PathVariable Long loanId,
			@Valid @RequestBody IssueCreateRequest request) {
		UUID staffUserId = getCurrentUserId();
		return ResponseEntity.ok(ApiResponse.<BookReturnResponse>builder()
				.data(returnService.createIssue(loanId, request, staffUserId))
				.build());
	}

	// 5. Update fine after return
	@PatchMapping("/returns/{loanId}/fines")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<BookReturnResponse>> updateFine(
			@PathVariable Long loanId,
			@Valid @RequestBody FineUpdateRequest request) {
		return ResponseEntity.ok(ApiResponse.<BookReturnResponse>builder()
				.data(returnService.updateFine(loanId, request))
				.build());
	}

	// 6. Send return receipt
	@PostMapping("/returns/{loanId}/send-receipt")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<String>> sendReceipt(@PathVariable Long loanId) {
		returnService.sendReceipt(loanId);
		return ResponseEntity.ok(ApiResponse.<String>builder()
				.data("Gửi biên nhận thành công qua email cho sinh viên")
				.build());
	}

	// 7. List of returns
	@GetMapping("/returns")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<PageResponse<BookReturnResponse>>> getAll(
			@RequestParam(required = false) UUID studentId,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
			@RequestParam(required = false) Boolean hasViolation,
			@PageableDefault(size = 5, sort = "returnDate", direction = Sort.Direction.DESC) Pageable pageable) {

		return ResponseEntity.ok(ApiResponse.<PageResponse<BookReturnResponse>>builder()
				.data(new PageResponse<>(returnService.getAllReturns(studentId, status, startDate, endDate, hasViolation, pageable)))
				.build());
	}

	// 8. Get return details
	@GetMapping("/returns/{returnId}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF') or hasRole('STUDENT')")
	public ResponseEntity<ApiResponse<BookReturnResponse>> getById(@PathVariable Long returnId) {
		return ResponseEntity.ok(ApiResponse.<BookReturnResponse>builder()
				.data(returnService.getReturnById(returnId))
				.build());
	}
}
