package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.reservation.ReservationResponse;
import com.quangtruong.library_management.entity.*;
import com.quangtruong.library_management.mapper.IReservationMapper;
import com.quangtruong.library_management.repository.*;
import com.quangtruong.library_management.service.impl.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

	@Mock
	IReservationRepository       reservationRepository;
	@Mock
	IReservationStatusRepository reservationStatusRepository;
	@Mock
	IStudentProfileRepository    studentProfileRepository;
	@Mock
	IBookCopyRepository          bookCopyRepository;
	@Mock
	IReservationMapper           reservationMapper;

	@InjectMocks
	ReservationService reservationService;

	UUID studentId = UUID.randomUUID();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void getMyReservations_Success() {
		Reservation reservation = Reservation.builder().id(1L).build();
		Page<Reservation> page = new PageImpl<>(List.of(reservation));

		when(reservationRepository.findByStudentProfileId(eq(studentId), any())).thenReturn(page);

		ReservationResponse expectedResponse = ReservationResponse.builder().id(1L).build();
		when(reservationMapper.toResponse(any(Reservation.class))).thenReturn(expectedResponse);

		Page<ReservationResponse> result = reservationService.getMyReservations(studentId, PageRequest.of(0, 10));

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals(1L, result.getContent().get(0).getId());
	}
}
