package com.quangtruong.library_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quangtruong.library_management.dto.bookcopy.BookCopyResponse;
import com.quangtruong.library_management.dto.bookcopy.BookCopyDetailResponse;
import com.quangtruong.library_management.dto.bookcopy.BookCopyStatusUpdateRequest;
import com.quangtruong.library_management.service.IBookCopyService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookCopyControllerTest {

	MockMvc mockMvc;

	@Mock
	IBookCopyService bookCopyService;

	@InjectMocks
	BookCopyController bookCopyController;

	ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(bookCopyController)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.build();
	}

	@Test
	void getBookCopies_Success() throws Exception {
		BookCopyResponse response = BookCopyResponse.builder().id(1L).qrCode("QR1").build();
		when(bookCopyService.getBookCopies(any(), any(), any())).thenReturn(new PageImpl<>(Collections.singletonList(response)));

		mockMvc.perform(get("/api/book-copies")
						.param("bookId", "1")
						.param("status", "AVAILABLE"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].qrCode").value("QR1"));
	}

	@Test
	void getBookCopyDetails_Success() throws Exception {
		BookCopyDetailResponse response = BookCopyDetailResponse.builder()
				.id(1L)
				.qrCode("QR1")
				.status("AVAILABLE")
				.bookName("Book A")
				.locationName("Location A")
				.build();
		when(bookCopyService.getBookCopyDetails(1L)).thenReturn(response);

		mockMvc.perform(get("/api/book-copies/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.qrCode").value("QR1"))
				.andExpect(jsonPath("$.data.bookName").value("Book A"));
	}

	@Test
	void updateCirculationStatus_Success() throws Exception {
		BookCopyStatusUpdateRequest request = new BookCopyStatusUpdateRequest("DAMAGED", null);

		mockMvc.perform(patch("/api/book-copies/1/circulation-status")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk());
	}

	@Test
	void deleteBookCopy_Success() throws Exception {
		mockMvc.perform(delete("/api/book-copies/1"))
				.andExpect(status().isOk());
	}
}
