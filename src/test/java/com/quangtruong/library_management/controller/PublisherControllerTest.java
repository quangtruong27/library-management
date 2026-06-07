package com.quangtruong.library_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quangtruong.library_management.dto.publisher.PublisherRequest;
import com.quangtruong.library_management.dto.publisher.PublisherResponse;
import com.quangtruong.library_management.service.IPublisherService;
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

class PublisherControllerTest {

	MockMvc mockMvc;

	@Mock
	IPublisherService publisherService;

	@InjectMocks
	PublisherController publisherController;

	ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(publisherController)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.build();
	}

	@Test
	void getAll_Success() throws Exception {
		PublisherResponse response = PublisherResponse.builder().id(1L).name("O'Reilly").build();
		when(publisherService.getAll(any())).thenReturn(new PageImpl<>(Collections.singletonList(response)));

		mockMvc.perform(get("/api/publishers"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].name").value("O'Reilly"));
	}

	@Test
	void getById_Success() throws Exception {
		PublisherResponse response = PublisherResponse.builder().id(1L).name("O'Reilly").build();
		when(publisherService.getById(1L)).thenReturn(response);

		mockMvc.perform(get("/api/publishers/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name").value("O'Reilly"));
	}

	@Test
	void create_Success() throws Exception {
		PublisherRequest request = new PublisherRequest("Pearson", null, null);
		PublisherResponse response = PublisherResponse.builder().id(2L).name("Pearson").build();

		when(publisherService.create(any())).thenReturn(response);

		mockMvc.perform(post("/api/publishers")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.name").value("Pearson"));
	}

	@Test
	void update_Success() throws Exception {
		PublisherRequest request = new PublisherRequest("Updated", null, null);
		PublisherResponse response = PublisherResponse.builder().id(1L).name("Updated").build();

		when(publisherService.update(anyLong(), any())).thenReturn(response);

		mockMvc.perform(put("/api/publishers/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name").value("Updated"));
	}

	@Test
	void delete_Success() throws Exception {
		mockMvc.perform(delete("/api/publishers/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value("Deleted successfully"));
	}
}
