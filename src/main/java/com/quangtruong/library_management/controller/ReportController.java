package com.quangtruong.library_management.controller;

import com.quangtruong.library_management.dto.ApiResponse;
import com.quangtruong.library_management.dto.report.*;
import com.quangtruong.library_management.service.IReportService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
public class ReportController {

	IReportService reportService;

	// 1. Summary of loans/returns
	@GetMapping("/loan-summary")
	public ResponseEntity<ApiResponse<LoanSummaryReportResponse>> getLoanSummary(
			@RequestParam(required = false, defaultValue = "month") String period,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

		return ResponseEntity.ok(ApiResponse.<LoanSummaryReportResponse>builder()
				.data(reportService.getLoanSummary(period, startDate, endDate))
				.build());
	}

	// 2. Top borrowed books
	@GetMapping("/top-books")
	public ResponseEntity<ApiResponse<List<TopBookReportResponse>>> getTopBooks(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
			@RequestParam(required = false, defaultValue = "10") int limit) {

		return ResponseEntity.ok(ApiResponse.<List<TopBookReportResponse>>builder()
				.data(reportService.getTopBooks(startDate, endDate, limit))
				.build());
	}

	// 3. Activity by faculty/class
	@GetMapping("/student-activity")
	public ResponseEntity<ApiResponse<List<StudentActivityReportResponse>>> getStudentActivity(
			@RequestParam(required = false) Long facultyId,
			@RequestParam(required = false) Long clazzId) {

		return ResponseEntity.ok(ApiResponse.<List<StudentActivityReportResponse>>builder()
				.data(reportService.getStudentActivity(facultyId, clazzId))
				.build());
	}

	// 4. Fine revenue breakdown
	@GetMapping("/fines-revenue")
	public ResponseEntity<ApiResponse<List<FineRevenueReportResponse>>> getFineRevenue(
			@RequestParam(required = false, defaultValue = "month") String period,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

		return ResponseEntity.ok(ApiResponse.<List<FineRevenueReportResponse>>builder()
				.data(reportService.getFineRevenue(period, startDate, endDate))
				.build());
	}

	// 5. Inventory report
	@GetMapping("/inventory")
	public ResponseEntity<ApiResponse<InventoryReportResponse>> getInventoryReport() {
		return ResponseEntity.ok(ApiResponse.<InventoryReportResponse>builder()
				.data(reportService.getInventoryReport())
				.build());
	}

	// 6. Export report file (PDF/Excel)
	@PostMapping("/export")
	public ResponseEntity<ApiResponse<ReportExportResponse>> exportReport(
			@Valid @RequestBody ReportExportRequest request) {

		return ResponseEntity.ok(ApiResponse.<ReportExportResponse>builder()
				.data(reportService.exportReport(request))
				.build());
	}
}
