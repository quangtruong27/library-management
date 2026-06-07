package com.quangtruong.library_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quangtruong.library_management.dto.book.*;
import com.quangtruong.library_management.service.IBookService;
import com.quangtruong.library_management.service.IReviewService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookControllerTest {

	MockMvc mockMvc;

	@Mock
	IBookService bookService;

	@Mock
	IReviewService reviewService;

	@InjectMocks
	BookController bookController;

	ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(bookController)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.build();
	}

	@Test
	void getAll_Success() throws Exception {
		BookResponse book = BookResponse.builder().id(1L).name("Java Design").build();
		when(bookService.searchBooks(any(), any(), any(), any()))
				.thenReturn(new PageImpl<>(Collections.singletonList(book)));

		mockMvc.perform(get("/api/books")
						.param("keyword", "Java")
						.param("page", "0")
						.param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].id").value(1L))
				.andExpect(jsonPath("$.data.content[0].name").value("Java Design"));
	}

	@Test
	void getById_Success() throws Exception {
		BookResponse book = BookResponse.builder().id(1L).name("Java Design").build();
		when(bookService.getBookDetails(1L)).thenReturn(book);

		mockMvc.perform(get("/api/books/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(1L));
	}
}
