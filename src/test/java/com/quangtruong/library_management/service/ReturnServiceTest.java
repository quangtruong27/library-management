package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.returns.*;
import com.quangtruong.library_management.entity.*;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.repository.*;
import com.quangtruong.library_management.service.impl.ReturnService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReturnServiceTest {

	@Mock
	IBorrowRepository borrowRepository;
	@Mock
	IBorrowDetailRepository borrowDetailRepository;
	@Mock
	IBookCopyRepository bookCopyRepository;
	@Mock
	IStudentProfileRepository studentProfileRepository;
	@Mock
	IStaffProfileRepository staffProfileRepository;
	@Mock
	IFineRepository fineRepository;
	@Mock
	IBookReturnRepository bookReturnRepository;

	@InjectMocks
	ReturnService returnService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void checkCondition_Success() {
		Book book = Book.builder().id(1L).name("Clean Code").price(BigDecimal.valueOf(100000)).build();
		BookCopy copy = BookCopy.builder().id(1L).book(book).build();
		BorrowDetail detail = BorrowDetail.builder().id(1L).bookCopy(copy).build();

		LocalDateTime borrowDate = LocalDateTime.now().minusDays(10);
		LocalDateTime dueDate = LocalDateTime.now().minusDays(3);

		Borrow borrow = Borrow.builder()
				.id(1L)
				.borrowDate(borrowDate)
				.dueDate(dueDate)
				.status("BORROWING")
				.borrowDetai(List.of(detail))
				.build();

		when(borrowRepository.findById(1L)).thenReturn(Optional.of(borrow));

		ConditionCheckRequest request = ConditionCheckRequest.builder()
				.condition("DAMAGED_LIGHT")
				.note("Torn page")
				.build();

		ConditionCheckResponse response = returnService.checkCondition(1L, request);

		assertNotNull(response);
		assertEquals(1L, response.getLoanId());
		assertEquals("DAMAGED_LIGHT", response.getCondition());
		assertEquals(BigDecimal.valueOf(15000L), response.getOverdueFine());
		assertEquals(BigDecimal.valueOf(10000.0), response.getEstimatedDamageFine());
		assertEquals(BigDecimal.valueOf(25000.0), response.getTotalEstimatedFine());
	}

	@Test
	void checkCondition_BorrowNotFound() {
		when(borrowRepository.findById(1L)).thenReturn(Optional.empty());

		ConditionCheckRequest request = ConditionCheckRequest.builder()
				.condition("NORMAL")
				.build();

		AppException exception = assertThrows(AppException.class, () -> {
			returnService.checkCondition(1L, request);
		});

		assertEquals(ErrorCode.BORROW_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	void confirmReturn_Success_Normal() {
		User studentUser = User.builder().id(UUID.randomUUID()).name("Student").build();
		StudentProfile student = StudentProfile.builder().id(UUID.randomUUID()).user(studentUser).studentCode("ST001").build();

		Book book = Book.builder().id(1L).name("Clean Code").price(BigDecimal.valueOf(100000)).build();
		BookCopy copy = BookCopy.builder().id(1L).book(book).status("BORROWED").build();
		BorrowDetail detail = BorrowDetail.builder().id(1L).bookCopy(copy).build();

		Borrow borrow = Borrow.builder()
				.id(1L)
				.borrowDate(LocalDateTime.now().minusDays(5))
				.dueDate(LocalDateTime.now().plusDays(2))
				.status("BORROWING")
				.studentProfile(student)
				.borrowDetai(List.of(detail))
				.build();

		detail.setBorrow(borrow);

		User staffUser = User.builder().id(UUID.randomUUID()).name("Staff").build();
		StaffProfile staff = StaffProfile.builder().id(UUID.randomUUID()).user(staffUser).build();

		when(borrowRepository.findById(1L)).thenReturn(Optional.of(borrow));
		when(staffProfileRepository.findById(staff.getId())).thenReturn(Optional.of(staff));
		when(bookReturnRepository.findByBorrowDetailId(1L)).thenReturn(Optional.empty());

		ReturnRequest request = ReturnRequest.builder()
				.loan(new ReturnRequest.LoanIdRequest(1L))
				.condition("NORMAL")
				.note("Returned in perfect condition")
				.build();

		BookReturnResponse response = returnService.confirmReturn(request, staff.getId());

		assertNotNull(response);
		assertEquals("RETURNED", response.getStatus());
		assertEquals("NORMAL", response.getBookCondition());
		assertEquals(BigDecimal.ZERO, response.getFineAmount());
		assertEquals("AVAILABLE", copy.getStatus());

		verify(bookReturnRepository, times(1)).save(any(BookReturn.class));
		verify(bookCopyRepository, times(1)).save(copy);
		verify(borrowRepository, times(1)).save(borrow);
	}
}
