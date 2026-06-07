package com.quangtruong.library_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quangtruong.library_management.dto.booklocation.BookLocationRequest;
import com.quangtruong.library_management.dto.booklocation.BookLocationResponse;
import com.quangtruong.library_management.service.IBookLocationService;
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

class BookLocationControllerTest {

	MockMvc mockMvc;

	@Mock
	IBookLocationService bookLocationService;

	@InjectMocks
	BookLocationController bookLocationController;

	ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(bookLocationController)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.build();
	}

	@Test
	void getAll_Success() throws Exception {
		BookLocationResponse response = BookLocationResponse.builder().id(1L).name("Shelf A").build();
		when(bookLocationService.getAll(any())).thenReturn(new PageImpl<>(Collections.singletonList(response)));

		mockMvc.perform(get("/api/book-locations"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].name").value("Shelf A"));
	}

	@Test
	void getById_Success() throws Exception {
		BookLocationResponse response = BookLocationResponse.builder().id(1L).name("Shelf A").build();
		when(bookLocationService.getById(1L)).thenReturn(response);

		mockMvc.perform(get("/api/book-locations/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name").value("Shelf A"));
	}

	@Test
	void create_Success() throws Exception {
		BookLocationRequest request = BookLocationRequest.builder().name("Shelf B").build();
		BookLocationResponse response = BookLocationResponse.builder().id(2L).name("Shelf B").build();

		when(bookLocationService.create(any())).thenReturn(response);

		mockMvc.perform(post("/api/book-locations")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.name").value("Shelf B"));
	}

	@Test
	void update_Success() throws Exception {
		BookLocationRequest request = BookLocationRequest.builder().name("Updated").build();
		BookLocationResponse response = BookLocationResponse.builder().id(1L).name("Updated").build();

		when(bookLocationService.update(anyLong(), any())).thenReturn(response);

		mockMvc.perform(put("/api/book-locations/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name").value("Updated"));
	}

	@Test
	void delete_Success() throws Exception {
		mockMvc.perform(delete("/api/book-locations/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value("Delete author successfully"));
	}
}
