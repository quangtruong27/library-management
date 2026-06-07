package com.quangtruong.library_management.controller;

import com.quangtruong.library_management.dto.borrow.BorrowResponse;
import com.quangtruong.library_management.dto.fine.FineResponse;
import com.quangtruong.library_management.dto.reservation.ReservationResponse;
import com.quangtruong.library_management.dto.student.StudentProfileResponse;
import com.quangtruong.library_management.entity.Borrow;
import com.quangtruong.library_management.entity.Fine;
import com.quangtruong.library_management.mapper.IBorrowMapper;
import com.quangtruong.library_management.mapper.IFineMapper;
import com.quangtruong.library_management.repository.IBorrowRepository;
import com.quangtruong.library_management.repository.IFineRepository;
import com.quangtruong.library_management.service.IReservationService;
import com.quangtruong.library_management.service.IStudentProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MeControllerTest {

	MockMvc mockMvc;

	@Mock
	IStudentProfileService studentProfileService;
	@Mock
	IReservationService reservationService;
	@Mock
	IBorrowRepository borrowRepository;
	@Mock
	IFineRepository fineRepository;
	@Mock
	IBorrowMapper borrowMapper;
	@Mock
	IFineMapper fineMapper;

	@Mock
	SecurityContext securityContext;
	@Mock
	Authentication authentication;

	@InjectMocks
	MeController meController;

	UUID userId = UUID.randomUUID();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		when(authentication.getName()).thenReturn(userId.toString());

		mockMvc = MockMvcBuilders.standaloneSetup(meController)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.build();
	}

	@Test
	void getMyProfile_Success() throws Exception {
		StudentProfileResponse response = StudentProfileResponse.builder().id(userId).name("Me").build();
		when(studentProfileService.getById(userId)).thenReturn(response);

		mockMvc.perform(get("/api/me/profile"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name").value("Me"));
	}

	@Test
	void getMyBorrowedItems_Success() throws Exception {
		Borrow borrow = Borrow.builder().id(1L).build();
		BorrowResponse response = BorrowResponse.builder().id(1L).status("BORROWING").build();

		when(borrowRepository.findByStudentProfileIdAndStatus(userId, "BORROWING")).thenReturn(Collections.singletonList(borrow));
		when(borrowMapper.toResponse(borrow)).thenReturn(response);

		mockMvc.perform(get("/api/me/borrowed-items"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].id").value(1L));
	}

	@Test
	void getMyBorrowHistory_Success() throws Exception {
		Borrow borrow = Borrow.builder().id(1L).build();
		BorrowResponse response = BorrowResponse.builder().id(1L).status("RETURNED").build();

		when(borrowRepository.findByStudentProfileId(userId)).thenReturn(Collections.singletonList(borrow));
		when(borrowMapper.toResponse(borrow)).thenReturn(response);

		mockMvc.perform(get("/api/me/borrow-history"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].id").value(1L));
	}

	@Test
	void getMyReservations_Success() throws Exception {
		ReservationResponse response = ReservationResponse.builder().id(1L).statusName("PENDING").build();
		when(reservationService.getMyReservations(eq(userId), any())).thenReturn(new PageImpl<>(Collections.singletonList(response)));

		mockMvc.perform(get("/api/me/reservations"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].id").value(1L));
	}

	@Test
	void getMyViolations_Success() throws Exception {
		Fine fine = Fine.builder().id(1L).build();
		FineResponse response = FineResponse.builder().id(1L).status("UNPAID").build();

		when(fineRepository.findByStudentProfileId(userId)).thenReturn(Collections.singletonList(fine));
		when(fineMapper.toResponse(fine)).thenReturn(response);

		mockMvc.perform(get("/api/me/violations"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].id").value(1L));
	}

	@Test
	void getMyFines_Success() throws Exception {
		Fine fine1 = Fine.builder().id(1L).referencePrice(BigDecimal.valueOf(10000)).status("UNPAID").build();
		Fine fine2 = Fine.builder().id(2L).referencePrice(BigDecimal.valueOf(15000)).status("UNPAID").build();

		when(fineRepository.findByStudentProfileIdAndStatus(userId, "UNPAID")).thenReturn(java.util.Arrays.asList(fine1, fine2));

		mockMvc.perform(get("/api/me/fines"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value(25000));
	}

	@Test
	void getMyFinePayments_Success() throws Exception {
		mockMvc.perform(get("/api/me/fine-payments"))
				.andExpect(status().isOk());
	}

	@Test
	void getMyNotifications_Success() throws Exception {
		mockMvc.perform(get("/api/me/notifications"))
				.andExpect(status().isOk());
	}
}
