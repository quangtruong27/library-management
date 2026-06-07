package com.quangtruong.library_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quangtruong.library_management.dto.borrow.*;
import com.quangtruong.library_management.service.IBorrowService;
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

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BorrowControllerTest {

	MockMvc mockMvc;

	@Mock
	IBorrowService borrowService;

	@InjectMocks
	BorrowController borrowController;

	@Mock
	SecurityContext securityContext;

	@Mock
	Authentication authentication;

	ObjectMapper objectMapper = new ObjectMapper();

	UUID studentId = UUID.randomUUID();
	UUID staffId = UUID.randomUUID();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		when(authentication.getName()).thenReturn(staffId.toString());

		mockMvc = MockMvcBuilders.standaloneSetup(borrowController)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.build();
	}

	@Test
	void getBorrowSession_Success() throws Exception {
		BorrowSessionResponse response = BorrowSessionResponse.builder()
				.studentId(studentId)
				.studentName("John")
				.build();

		when(borrowService.getBorrowSession(studentId)).thenReturn(response);

		mockMvc.perform(get("/api/students/" + studentId + "/borrow-session"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.studentId").value(studentId.toString()))
				.andExpect(jsonPath("$.data.studentName").value("John"));
	}

	@Test
	void checkEligibility_Success() throws Exception {
		CheckEligibilityRequest request = CheckEligibilityRequest.builder()
				.student(new CheckEligibilityRequest.StudentIdRequest(studentId))
				.copy(new CheckEligibilityRequest.CopyIdRequest(1L))
				.build();

		CheckEligibilityResponse response = CheckEligibilityResponse.builder()
				.eligible(true)
				.reasons(Collections.emptyList())
				.build();

		when(borrowService.checkEligibility(any())).thenReturn(response);

		mockMvc.perform(post("/api/loans/check-eligibility")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.eligible").value(true));
	}

	@Test
	void createBorrow_Success() throws Exception {
		String requestJson = "{\n" +
				"  \"student\": {\n" +
				"    \"id\": \"" + studentId + "\"\n" +
				"  },\n" +
				"  \"copy\": {\n" +
				"    \"id\": 1\n" +
				"  },\n" +
				"  \"dueDate\": \"2026-06-15T17:00:00\",\n" +
				"  \"note\": \"Direct borrow\"\n" +
				"}";

		BorrowResponse response = BorrowResponse.builder()
				.id(1L)
				.status("BORROWING")
				.build();

		when(borrowService.createBorrow(any(), eq(staffId))).thenReturn(response);

		mockMvc.perform(post("/api/loans")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.id").value(1L))
				.andExpect(jsonPath("$.data.status").value("BORROWING"));
	}

	@Test
	void confirmLoanPickup_Success() throws Exception {
		BorrowResponse response = BorrowResponse.builder()
				.id(1L)
				.status("BORROWING")
				.build();

		when(borrowService.confirmLoanPickup(1L)).thenReturn(response);

		mockMvc.perform(post("/api/loans/1/confirm-pickup"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(1L))
				.andExpect(jsonPath("$.data.status").value("BORROWING"));
	}

	@Test
	void getById_Success() throws Exception {
		BorrowResponse response = BorrowResponse.builder()
				.id(1L)
				.status("BORROWING")
				.build();

		when(borrowService.getBorrowById(1L)).thenReturn(response);

		mockMvc.perform(get("/api/loans/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(1L));
	}

	@Test
	void deleteBorrow_Success() throws Exception {
		doNothing().when(borrowService).deleteBorrow(1L);

		mockMvc.perform(delete("/api/loans/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value("Hủy phiếu mượn thành công"));
	}
}
