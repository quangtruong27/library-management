package com.quangtruong.library_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quangtruong.library_management.dto.author.AuthorRequest;
import com.quangtruong.library_management.dto.author.AuthorResponse;
import com.quangtruong.library_management.mapper.IAuthorMapper;
import com.quangtruong.library_management.service.IAuthorService;
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

class AuthorControllerTest {

	MockMvc mockMvc;

	@Mock
	IAuthorService authorService;

	@Mock
	IAuthorMapper authorMapper;

	@InjectMocks
	AuthorController authorController;

	ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(authorController)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.build();
	}

	@Test
	void getById_Success() throws Exception {
		AuthorResponse response = AuthorResponse.builder().id(1L).name("Author A").build();
		when(authorService.getById(1L)).thenReturn(response);

		mockMvc.perform(get("/api/authors/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name").value("Author A"));
	}

	@Test
	void getAll_Success() throws Exception {
		AuthorResponse response = AuthorResponse.builder().id(1L).name("Author A").build();
		when(authorService.getAll(any())).thenReturn(new PageImpl<>(Collections.singletonList(response)));

		mockMvc.perform(get("/api/authors"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].name").value("Author A"));
	}

	@Test
	void create_Success() throws Exception {
		AuthorRequest request = AuthorRequest.builder().name("Author B").build();
		AuthorResponse response = AuthorResponse.builder().id(2L).name("Author B").build();

		when(authorService.createAuthor(any())).thenReturn(response);

		mockMvc.perform(post("/api/authors")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.name").value("Author B"));
	}

	@Test
	void update_Success() throws Exception {
		AuthorRequest request = AuthorRequest.builder().name("Author Updated").build();
		AuthorResponse response = AuthorResponse.builder().id(1L).name("Author Updated").build();

		when(authorService.updateAuthor(anyLong(), any())).thenReturn(response);

		mockMvc.perform(put("/api/authors/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name").value("Author Updated"));
	}

	@Test
	void delete_Success() throws Exception {
		mockMvc.perform(delete("/api/authors/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value("Delete author successfully"));
	}
}
