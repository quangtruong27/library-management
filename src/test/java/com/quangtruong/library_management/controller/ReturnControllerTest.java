package com.quangtruong.library_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quangtruong.library_management.dto.returns.*;
import com.quangtruong.library_management.service.IReturnService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReturnControllerTest {

	MockMvc mockMvc;

	@Mock
	IReturnService returnService;

	@InjectMocks
	ReturnController returnController;

	@Mock
	SecurityContext securityContext;

	@Mock
	Authentication authentication;

	ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		when(authentication.getName()).thenReturn(UUID.randomUUID().toString());

		mockMvc = MockMvcBuilders.standaloneSetup(returnController)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.build();
	}

	@Test
	void scanReturn_Success() throws Exception {
		ReturnScanRequest request = ReturnScanRequest.builder()
				.student(new ReturnScanRequest.StudentIdRequest(UUID.randomUUID()))
				.copy(new ReturnScanRequest.CopyIdRequest(1L))
				.build();

		ReturnScanResponse response = ReturnScanResponse.builder()
				.loanId(1L)
				.studentName("John")
				.bookName("Clean Code")
				.build();

		when(returnService.scanReturn(any())).thenReturn(response);

		mockMvc.perform(post("/api/returns/scan")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.loanId").value(1L))
				.andExpect(jsonPath("$.data.bookName").value("Clean Code"));
	}

	@Test
	void confirmReturn_Success() throws Exception {
		ReturnRequest request = ReturnRequest.builder()
				.loan(new ReturnRequest.LoanIdRequest(1L))
				.condition("NORMAL")
				.build();

		BookReturnResponse response = BookReturnResponse.builder()
				.id(1L)
				.status("RETURNED")
				.bookCondition("NORMAL")
				.build();

		when(returnService.confirmReturn(any(), any())).thenReturn(response);

		mockMvc.perform(post("/api/returns")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(1L))
				.andExpect(jsonPath("$.data.status").value("RETURNED"));
	}

	@Test
	void checkCondition_Success() throws Exception {
		ConditionCheckRequest request = ConditionCheckRequest.builder()
				.condition("DAMAGED_LIGHT")
				.build();

		ConditionCheckResponse response = ConditionCheckResponse.builder()
				.loanId(1L)
				.condition("DAMAGED_LIGHT")
				.totalEstimatedFine(BigDecimal.valueOf(15000L))
				.build();

		when(returnService.checkCondition(anyLong(), any())).thenReturn(response);

		mockMvc.perform(post("/api/returns/1/condition-check")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.loanId").value(1L))
				.andExpect(jsonPath("$.data.totalEstimatedFine").value(15000));
	}
}
