package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.borrow.*;
import com.quangtruong.library_management.entity.*;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.mapper.IBorrowMapper;
import com.quangtruong.library_management.mapper.IFineMapper;
import com.quangtruong.library_management.mapper.IReservationMapper;
import com.quangtruong.library_management.repository.*;
import com.quangtruong.library_management.service.impl.BorrowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class BorrowServiceTest {

	@Mock
	IBorrowRepository borrowRepository;
	@Mock
	IBorrowDetailRepository borrowDetailRepository;
	@Mock
	IStudentProfileRepository studentProfileRepository;
	@Mock
	IStaffProfileRepository staffProfileRepository;
	@Mock
	IBookCopyRepository bookCopyRepository;
	@Mock
	IReservationRepository reservationRepository;
	@Mock
	IReservationStatusRepository reservationStatusRepository;
	@Mock
	IFineRepository fineRepository;
	@Mock
	IBorrowMapper borrowMapper;
	@Mock
	IReservationMapper reservationMapper;
	@Mock
	IFineMapper fineMapper;

	@InjectMocks
	BorrowService borrowService;

	UUID studentId = UUID.randomUUID();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void getBorrowSession_Success() {
		User user = User.builder().id(studentId).name("Jane Doe").build();
		StudentProfile student = StudentProfile.builder()
				.id(studentId)
				.user(user)
				.studentCode("ST123")
				.build();

		when(studentProfileRepository.findById(studentId)).thenReturn(Optional.of(student));
		when(reservationRepository.findByStudentProfileIdAndStatusNameIn(any(), any())).thenReturn(Collections.emptyList());
		when(borrowRepository.findByStudentProfileIdAndStatus(any(), any())).thenReturn(Collections.emptyList());
		when(fineRepository.findByStudentProfileIdAndStatus(any(), any())).thenReturn(Collections.emptyList());

		BorrowSessionResponse response = borrowService.getBorrowSession(studentId);

		assertNotNull(response);
		assertEquals(studentId, response.getStudentId());
		assertEquals("Jane Doe", response.getStudentName());
		assertEquals("ST123", response.getStudentCode());
		assertEquals(0L, response.getCurrentBorrowCount());
		assertEquals(BigDecimal.ZERO, response.getUnpaidFinesTotal());
	}

	@Test
	void getBorrowSession_StudentNotFound() {
		when(studentProfileRepository.findById(studentId)).thenReturn(Optional.empty());

		assertThrows(AppException.class, () -> borrowService.getBorrowSession(studentId));
	}
}
