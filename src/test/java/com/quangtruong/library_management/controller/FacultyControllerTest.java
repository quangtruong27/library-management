package com.quangtruong.library_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quangtruong.library_management.dto.faculty.FacultyRequest;
import com.quangtruong.library_management.dto.faculty.FacultyResponse;
import com.quangtruong.library_management.service.IFacultyService;
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

class FacultyControllerTest {

	MockMvc mockMvc;

	@Mock
	IFacultyService facultyService;

	@InjectMocks
	FacultyController facultyController;

	ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(facultyController)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.build();
	}

	@Test
	void getAll_Success() throws Exception {
		FacultyResponse response = FacultyResponse.builder().id(1L).name("IT").build();
		when(facultyService.getAll(any())).thenReturn(new PageImpl<>(Collections.singletonList(response)));

		mockMvc.perform(get("/api/faculties"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].name").value("IT"));
	}

	@Test
	void getById_Success() throws Exception {
		FacultyResponse response = FacultyResponse.builder().id(1L).name("IT").build();
		when(facultyService.getById(1L)).thenReturn(response);

		mockMvc.perform(get("/api/faculties/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name").value("IT"));
	}

	@Test
	void create_Success() throws Exception {
		FacultyRequest request = new FacultyRequest("Business");
		FacultyResponse response = FacultyResponse.builder().id(2L).name("Business").build();

		when(facultyService.create(any())).thenReturn(response);

		mockMvc.perform(post("/api/faculties")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.name").value("Business"));
	}

	@Test
	void update_Success() throws Exception {
		FacultyRequest request = new FacultyRequest("UPDATED");
		FacultyResponse response = FacultyResponse.builder().id(1L).name("UPDATED").build();

		when(facultyService.update(anyLong(), any())).thenReturn(response);

		mockMvc.perform(put("/api/faculties/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name").value("UPDATED"));
	}

	@Test
	void delete_Success() throws Exception {
		mockMvc.perform(delete("/api/faculties/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value("Deleted successfully"));
	}
}
