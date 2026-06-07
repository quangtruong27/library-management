package com.quangtruong.library_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quangtruong.library_management.dto.fine.*;
import com.quangtruong.library_management.service.IFineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FineControllerTest {

	MockMvc mockMvc;

	@Mock
	IFineService fineService;

	@InjectMocks
	FineController fineController;

	ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(fineController)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.build();
	}

	@Test
	void getAllFines_Success() throws Exception {
		FineDetailResponse fine = FineDetailResponse.builder()
				.id(1L)
				.fineType("OVERDUE")
				.amount(BigDecimal.valueOf(20000L))
				.status("UNPAID")
				.build();

		when(fineService.getAllFines(any(), any(), any(), any())).thenReturn(new PageImpl<>(Collections.singletonList(fine)));

		mockMvc.perform(get("/api/fines")
						.param("page", "0")
						.param("size", "5"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].id").value(1L))
				.andExpect(jsonPath("$.data.content[0].fineType").value("OVERDUE"));
	}

	@Test
	void createFineManual_Success() throws Exception {
		FineCreateRequest request = FineCreateRequest.builder()
				.student(new FineCreateRequest.StudentIdRequest(UUID.randomUUID()))
				.fineType("DAMAGE")
				.amount(BigDecimal.valueOf(50000L))
				.build();

		FineDetailResponse response = FineDetailResponse.builder()
				.id(1L)
				.fineType("DAMAGE")
				.amount(BigDecimal.valueOf(50000L))
				.status("UNPAID")
				.build();

		when(fineService.createFineManual(any())).thenReturn(response);

		mockMvc.perform(post("/api/fines")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.id").value(1L))
				.andExpect(jsonPath("$.data.fineType").value("DAMAGE"));
	}

	@Test
	void getFineBalance_Success() throws Exception {
		UUID studentId = UUID.randomUUID();
		FineBalanceResponse response = FineBalanceResponse.builder()
				.studentId(studentId)
				.studentName("Jane")
				.balance(BigDecimal.valueOf(40000L))
				.build();

		when(fineService.getFineBalance(studentId)).thenReturn(response);

		mockMvc.perform(get("/api/students/" + studentId + "/fine-balance"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.studentName").value("Jane"))
				.andExpect(jsonPath("$.data.balance").value(40000));
	}
}
