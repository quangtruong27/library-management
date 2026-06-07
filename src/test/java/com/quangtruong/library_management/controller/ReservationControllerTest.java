package com.quangtruong.library_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quangtruong.library_management.dto.borrow.BorrowResponse;
import com.quangtruong.library_management.dto.reservation.ReservationRequest;
import com.quangtruong.library_management.dto.reservation.ReservationResponse;
import com.quangtruong.library_management.service.IBorrowService;
import com.quangtruong.library_management.service.IReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReservationControllerTest {

	MockMvc mockMvc;

	@Mock
	IReservationService reservationService;

	@Mock
	IBorrowService borrowService;

	@InjectMocks
	ReservationController reservationController;

	@Mock
	SecurityContext securityContext;

	@Mock
	Authentication authentication;

	ObjectMapper objectMapper = new ObjectMapper();

	UUID userId = UUID.randomUUID();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		when(authentication.getName()).thenReturn(userId.toString());

		mockMvc = MockMvcBuilders.standaloneSetup(reservationController)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.build();
	}

	@Test
	void getAll_Success() throws Exception {
		ReservationResponse reservation = ReservationResponse.builder().id(1L).build();
		when(reservationService.getAll(any())).thenReturn(new PageImpl<>(Collections.singletonList(reservation)));

		mockMvc.perform(get("/api/reservations")
						.param("page", "0")
						.param("size", "5"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].id").value(1L));
	}

	@Test
	void getById_Success() throws Exception {
		ReservationResponse reservation = ReservationResponse.builder().id(1L).build();
		when(reservationService.getById(1L)).thenReturn(reservation);

		mockMvc.perform(get("/api/reservations/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(1L));
	}

	@Test
	void create_Success() throws Exception {
		ReservationRequest request = new ReservationRequest();
		request.setBook(new ReservationRequest.BookIdRequest(1L));

		ReservationResponse response = ReservationResponse.builder().id(1L).build();
		when(reservationService.create(any(), eq(userId))).thenReturn(response);

		mockMvc.perform(post("/api/reservations")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.id").value(1L));
	}

	@Test
	void cancel_AsStudent_Success() throws Exception {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_STUDENT"));
		doReturn(authorities).when(authentication).getAuthorities();

		doNothing().when(reservationService).cancel(1L, userId);

		mockMvc.perform(delete("/api/reservations/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value("Hủy đặt trước thành công"));

		verify(reservationService, times(1)).cancel(1L, userId);
	}

	@Test
	void confirmPickup_Success() throws Exception {
		BorrowResponse response = BorrowResponse.builder().id(1L).status("BORROWING").build();
		when(borrowService.confirmReservationPickup(1L, userId)).thenReturn(response);

		mockMvc.perform(post("/api/reservations/1/confirm-pickup"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(1L))
				.andExpect(jsonPath("$.data.status").value("BORROWING"));
	}
}
