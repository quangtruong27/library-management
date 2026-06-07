package com.quangtruong.library_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quangtruong.library_management.dto.report.*;
import com.quangtruong.library_management.service.IReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReportControllerTest {

	MockMvc mockMvc;

	@Mock
	IReportService reportService;

	@InjectMocks
	ReportController reportController;

	ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(reportController).build();
	}

	@Test
	void getLoanSummary_Success() throws Exception {
		LoanSummaryReportResponse response = LoanSummaryReportResponse.builder()
				.totalBorrows(10)
				.totalReturns(8)
				.onTimeReturnRate(80.0)
				.overdueBorrows(2)
				.build();

		when(reportService.getLoanSummary(any(), any(), any())).thenReturn(response);

		mockMvc.perform(get("/api/reports/loan-summary"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.totalBorrows").value(10))
				.andExpect(jsonPath("$.data.onTimeReturnRate").value(80.0));
	}

	@Test
	void getTopBooks_Success() throws Exception {
		TopBookReportResponse topBook = TopBookReportResponse.builder()
				.bookId(1L)
				.name("Clean Code")
				.borrowCount(5)
				.build();

		when(reportService.getTopBooks(any(), any(), anyInt())).thenReturn(Collections.singletonList(topBook));

		mockMvc.perform(get("/api/reports/top-books"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].bookId").value(1L))
				.andExpect(jsonPath("$.data[0].name").value("Clean Code"));
	}

	@Test
	void getInventoryReport_Success() throws Exception {
		InventoryReportResponse response = InventoryReportResponse.builder()
				.totalBooks(5)
				.totalCopies(10)
				.availableCopies(7)
				.build();

		when(reportService.getInventoryReport()).thenReturn(response);

		mockMvc.perform(get("/api/reports/inventory"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.totalBooks").value(5))
				.andExpect(jsonPath("$.data.availableCopies").value(7));
	}

	@Test
	void exportReport_Success() throws Exception {
		ReportExportRequest request = ReportExportRequest.builder()
				.reportType("inventory")
				.format("pdf")
				.build();

		ReportExportResponse response = ReportExportResponse.builder()
				.downloadUrl("/api/reports/download/report.pdf")
				.message("Generated")
				.build();

		when(reportService.exportReport(any())).thenReturn(response);

		mockMvc.perform(post("/api/reports/export")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.downloadUrl").value("/api/reports/download/report.pdf"));
	}
}
