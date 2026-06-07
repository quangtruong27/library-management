package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.fine.*;
import com.quangtruong.library_management.entity.*;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.repository.*;
import com.quangtruong.library_management.service.impl.FineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FineServiceTest {

	@Mock
	IFineRepository fineRepository;
	@Mock
	IPaymentRepository paymentRepository;
	@Mock
	IPaymentDetailRepository paymentDetailRepository;
	@Mock
	IStudentProfileRepository studentProfileRepository;
	@Mock
	IStaffProfileRepository staffProfileRepository;

	@InjectMocks
	FineService fineService;

	UUID studentId = UUID.randomUUID();
	UUID staffId = UUID.randomUUID();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void createFineManual_Success() {
		User user = User.builder().id(studentId).name("Student User").build();
		StudentProfile student = StudentProfile.builder().id(studentId).user(user).studentCode("ST001").build();

		when(studentProfileRepository.findById(studentId)).thenReturn(Optional.of(student));

		FineCreateRequest.StudentIdRequest studentReq = new FineCreateRequest.StudentIdRequest(studentId);
		FineCreateRequest request = FineCreateRequest.builder()
				.student(studentReq)
				.fineType("OVERDUE")
				.amount(BigDecimal.valueOf(25000L))
				.dueDate(LocalDateTime.now().plusDays(5))
				.note("Manual fine")
				.build();

		FineDetailResponse response = fineService.createFineManual(request);

		assertNotNull(response);
		assertEquals(BigDecimal.valueOf(25000L), response.getAmount());
		assertEquals("UNPAID", response.getStatus());
		verify(fineRepository, times(1)).save(any(Fine.class));
	}

	@Test
	void createFineManual_StudentNotFound() {
		FineCreateRequest request = FineCreateRequest.builder()
				.student(new FineCreateRequest.StudentIdRequest(studentId))
				.amount(BigDecimal.valueOf(10000L))
				.build();

		when(studentProfileRepository.findById(studentId)).thenReturn(Optional.empty());

		assertThrows(AppException.class, () -> fineService.createFineManual(request));
	}

	@Test
	void recordPayment_PartialPayment() {
		User studentUser = User.builder().id(studentId).name("Student").build();
		StudentProfile student = StudentProfile.builder().id(studentId).user(studentUser).studentCode("ST001").build();

		Fine fine = Fine.builder()
				.id(1L)
				.fineType("OVERDUE")
				.referencePrice(BigDecimal.valueOf(50000L))
				.status("UNPAID")
				.studentProfile(student)
				.build();

		User staffUser = User.builder().id(staffId).name("Staff").build();
		StaffProfile staff = StaffProfile.builder().id(staffId).user(staffUser).build();

		when(fineRepository.findById(1L)).thenReturn(Optional.of(fine));
		when(staffProfileRepository.findById(staffId)).thenReturn(Optional.of(staff));
		when(paymentDetailRepository.findByFineId(1L)).thenReturn(Collections.emptyList());

		PaymentRequest request = PaymentRequest.builder()
				.paymentMethod("MOMO")
				.amount(BigDecimal.valueOf(20000L))
				.build();

		PaymentResponse response = fineService.recordPayment(1L, request, staffId);

		assertNotNull(response);
		assertEquals(BigDecimal.valueOf(20000L), response.getAmount());
		assertEquals("UNPAID", fine.getStatus()); // remains unpaid since 20k < 50k
		verify(paymentRepository, times(1)).save(any(Payment.class));
		verify(paymentDetailRepository, times(1)).save(any(PaymentDetail.class));
	}

	@Test
	void recordPayment_FullPayment() {
		User studentUser = User.builder().id(studentId).name("Student").build();
		StudentProfile student = StudentProfile.builder().id(studentId).user(studentUser).studentCode("ST001").build();

		Fine fine = Fine.builder()
				.id(1L)
				.fineType("DAMAGE")
				.referencePrice(BigDecimal.valueOf(30000L))
				.status("UNPAID")
				.studentProfile(student)
				.build();

		User staffUser = User.builder().id(staffId).name("Staff").build();
		StaffProfile staff = StaffProfile.builder().id(staffId).user(staffUser).build();

		when(fineRepository.findById(1L)).thenReturn(Optional.of(fine));
		when(staffProfileRepository.findById(staffId)).thenReturn(Optional.of(staff));
		when(paymentDetailRepository.findByFineId(1L)).thenReturn(Collections.emptyList());

		PaymentRequest request = PaymentRequest.builder()
				.paymentMethod("CASH")
				.amount(BigDecimal.valueOf(30000L))
				.build();

		PaymentResponse response = fineService.recordPayment(1L, request, staffId);

		assertNotNull(response);
		assertEquals("PAID", fine.getStatus()); // status updated to PAID
		verify(fineRepository, times(1)).save(fine);
	}

	@Test
	void getFineBalance_Success() {
		User studentUser = User.builder().id(studentId).name("Student").build();
		StudentProfile student = StudentProfile.builder().id(studentId).user(studentUser).studentCode("ST001").build();

		Fine fine1 = Fine.builder().id(1L).referencePrice(BigDecimal.valueOf(30000L)).status("UNPAID").studentProfile(student).build();
		Fine fine2 = Fine.builder().id(2L).referencePrice(BigDecimal.valueOf(20000L)).status("UNPAID").studentProfile(student).build();

		when(studentProfileRepository.findById(studentId)).thenReturn(Optional.of(student));
		when(fineRepository.findByStudentProfileIdAndStatus(studentId, "UNPAID")).thenReturn(List.of(fine1, fine2));
		when(paymentDetailRepository.findByFineId(1L)).thenReturn(Collections.emptyList());
		when(paymentDetailRepository.findByFineId(2L)).thenReturn(Collections.emptyList());

		FineBalanceResponse response = fineService.getFineBalance(studentId);

		assertNotNull(response);
		assertEquals(BigDecimal.valueOf(50000L), response.getBalance());
	}
}
