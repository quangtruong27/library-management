package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.report.*;

import java.time.LocalDateTime;
import java.util.List;

public interface IReportService {
	LoanSummaryReportResponse getLoanSummary(String period, LocalDateTime startDate, LocalDateTime endDate);
	List<TopBookReportResponse> getTopBooks(LocalDateTime startDate, LocalDateTime endDate, int limit);
	List<StudentActivityReportResponse> getStudentActivity(Long facultyId, Long clazzId);
	List<FineRevenueReportResponse> getFineRevenue(String period, LocalDateTime startDate, LocalDateTime endDate);
	InventoryReportResponse getInventoryReport();
	ReportExportResponse exportReport(ReportExportRequest request);
}
