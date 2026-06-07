package com.quangtruong.library_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quangtruong.library_management.dto.clazz.ClazzRequest;
import com.quangtruong.library_management.dto.clazz.ClazzResponse;
import com.quangtruong.library_management.service.IClazzService;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ClazzControllerTest {

	MockMvc mockMvc;

	@Mock
	IClazzService clazzService;

	@InjectMocks
	ClazzController clazzController;

	ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(clazzController)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.build();
	}

	@Test
	void getAll_Success() throws Exception {
		ClazzResponse response = ClazzResponse.builder().id(1L).name("Class A").build();
		when(clazzService.getAll(any())).thenReturn(new PageImpl<>(Collections.singletonList(response)));

		mockMvc.perform(get("/api/classes"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].name").value("Class A"));
	}

	@Test
	void getById_Success() throws Exception {
		ClazzResponse response = ClazzResponse.builder().id(1L).name("Class A").build();
		when(clazzService.getById(1L)).thenReturn(response);

		mockMvc.perform(get("/api/classes/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name").value("Class A"));
	}

	@Test
	void create_Success() throws Exception {
		ClazzRequest request = new ClazzRequest("Class B", null, null);
		ClazzResponse response = ClazzResponse.builder().id(2L).name("Class B").build();

		when(clazzService.create(any())).thenReturn(response);

		mockMvc.perform(post("/api/classes")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.name").value("Class B"));
	}

	@Test
	void update_Success() throws Exception {
		ClazzRequest request = new ClazzRequest("UPDATED", null, null);
		ClazzResponse response = ClazzResponse.builder().id(1L).name("UPDATED").build();

		when(clazzService.update(anyLong(), any())).thenReturn(response);

		mockMvc.perform(put("/api/classes/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name").value("UPDATED"));
	}

	@Test
	void delete_Success() throws Exception {
		mockMvc.perform(delete("/api/classes/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value("Deleted successfully"));
	}
}
