package com.quangtruong.library_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quangtruong.library_management.dto.category.CategoryRequest;
import com.quangtruong.library_management.dto.category.CategoryResponse;
import com.quangtruong.library_management.service.impl.CategoryService;
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

class CategoryControllerTest {

	MockMvc mockMvc;

	@Mock
	CategoryService categoryService;

	@InjectMocks
	CategoryController categoryController;

	ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.build();
	}

	@Test
	void getById_Success() throws Exception {
		CategoryResponse response = CategoryResponse.builder().id(1L).name("Sci-Fi").build();
		when(categoryService.getById(1L)).thenReturn(response);

		mockMvc.perform(get("/api/categories/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name").value("Sci-Fi"));
	}

	@Test
	void getAll_Success() throws Exception {
		CategoryResponse response = CategoryResponse.builder().id(1L).name("Sci-Fi").build();
		when(categoryService.getAll(any())).thenReturn(new PageImpl<>(Collections.singletonList(response)));

		mockMvc.perform(get("/api/categories"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].name").value("Sci-Fi"));
	}

	@Test
	void create_Success() throws Exception {
		CategoryRequest request = CategoryRequest.builder().name("Science").build();
		CategoryResponse response = CategoryResponse.builder().id(2L).name("Science").build();

		when(categoryService.create(any())).thenReturn(response);

		mockMvc.perform(post("/api/categories")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.name").value("Science"));
	}

	@Test
	void update_Success() throws Exception {
		CategoryRequest request = CategoryRequest.builder().name("Updated").build();
		CategoryResponse response = CategoryResponse.builder().id(1L).name("Updated").build();

		when(categoryService.update(anyLong(), any())).thenReturn(response);

		mockMvc.perform(put("/api/categories/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name").value("Updated"));
	}

	@Test
	void delete_Success() throws Exception {
		mockMvc.perform(delete("/api/categories/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value("Delete category successfully"));
	}
}
