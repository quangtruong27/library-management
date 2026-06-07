package com.quangtruong.library_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quangtruong.library_management.dto.student.StudentProfileResponse;
import com.quangtruong.library_management.service.IStudentProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StudentProfileControllerTest {

	MockMvc mockMvc;

	@Mock
	IStudentProfileService studentProfileService;

	@InjectMocks
	StudentProfileController studentProfileController;

	ObjectMapper objectMapper = new ObjectMapper();

	UUID studentId = UUID.randomUUID();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(studentProfileController)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.build();
	}

	@Test
	void getAll_Success() throws Exception {
		StudentProfileResponse student = StudentProfileResponse.builder().id(studentId).build();
		when(studentProfileService.getAll(any())).thenReturn(new PageImpl<>(Collections.singletonList(student)));

		mockMvc.perform(get("/api/students")
						.param("page", "0")
						.param("size", "5"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].id").value(studentId.toString()));
	}

	@Test
	void getById_Success() throws Exception {
		StudentProfileResponse student = StudentProfileResponse.builder().id(studentId).build();
		when(studentProfileService.getById(studentId)).thenReturn(student);

		mockMvc.perform(get("/api/students/" + studentId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(studentId.toString()));
	}

	@Test
	void updateStatus_Success() throws Exception {
		doNothing().when(studentProfileService).updateStatus(studentId, 1L);

		mockMvc.perform(patch("/api/students/" + studentId + "/status")
						.param("statusId", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value("Cập nhật trạng thái thành công"));
	}

	@Test
	void resetPassword_Success() throws Exception {
		doNothing().when(studentProfileService).resetPassword(studentId);

		mockMvc.perform(post("/api/students/" + studentId + "/reset-password"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value("Đã reset mật khẩu về mặc định (123456)"));
	}
}
