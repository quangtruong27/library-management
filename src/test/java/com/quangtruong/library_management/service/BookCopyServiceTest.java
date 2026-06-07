package com.quangtruong.librarymanagement.service.impl;

import com.quangtruong.librarymanagement.dto.book.BookCopyResponse;
import com.quangtruong.librarymanagement.dto.bookcopy.BookCopyDetailResponse;
import com.quangtruong.librarymanagement.dto.bookcopy.BookCopyStatusUpdateRequest;
import com.quangtruong.librarymanagement.dto.borrow.BorrowHistoryResponse;
import com.quangtruong.librarymanagement.entity.*;
import com.quangtruong.librarymanagement.exception.AppException;
import com.quangtruong.librarymanagement.exception.ErrorCode;
import com.quangtruong.librarymanagement.mapper.IBookCopyMapper;
import com.quangtruong.librarymanagement.repository.IBookCopyRepository;
import com.quangtruong.librarymanagement.repository.IBorrowDetailRepository;
import com.quangtruong.librarymanagement.service.IBookCopyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class BookCopyServiceTest {

	@Mock
	IBookCopyRepository bookCopyRepository;

	@Mock
	IBorrowDetailRepository borrowDetailRepository;

	@Mock
	IBookCopyMapper bookCopyMapper;

	@InjectMocks
	BookCopyService bookCopyService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void getBookCopies_Success() {
		BookCopy copy = BookCopy.builder().id(1L).qrCode("QR1").build();
		BookCopyResponse response = BookCopyResponse.builder().id(1L).qrCode("QR1").build();
		Page<BookCopy> page = new PageImpl<>(Collections.singletonList(copy));

		when(bookCopyRepository.findCopies(anyLong(), any(), any(Pageable.class))).thenReturn(page);
		when(bookCopyMapper.toResponse(copy)).thenReturn(response);

		Page<BookCopyResponse> result = bookCopyService.getBookCopies(1L, "AVAILABLE", PageRequest.of(0, 5));

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals("QR1", result.getContent().get(0).getQrCode());
	}

	@Test
	void getBookCopyDetails_Success() {
		Book book = Book.builder().id(1L).name("Book A").build();
		BookLocation location = BookLocation.builder().id(1L).name("Location A").build();
		BookCopy copy = BookCopy.builder()
				.id(1L)
				.qrCode("QR1")
				.status("AVAILABLE")
				.book(book)
				.bookLocation(location)
				.build();

		User user = User.builder().name("Student User").build();
		StudentProfile studentProfile = StudentProfile.builder().studentCode("STU001").user(user).build();
		Borrow borrow = Borrow.builder()
				.id(1L)
				.borrowDate(LocalDateTime.now())
				.dueDate(LocalDateTime.now().plusDays(14))
				.studentProfile(studentProfile)
				.build();

		BorrowDetail borrowDetail = BorrowDetail.builder()
				.borrow(borrow)
				.returnDate(LocalDateTime.now().plusDays(7))
				.note("Returned on time")
				.build();

		when(bookCopyRepository.findById(1L)).thenReturn(Optional.of(copy));
		when(borrowDetailRepository.findByBookCopyIdOrderByBorrowBorrowDateDesc(1L))
				.thenReturn(Collections.singletonList(borrowDetail));

		BookCopyDetailResponse result = bookCopyService.getBookCopyDetails(1L);

		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("QR1", result.getQrCode());
		assertEquals("AVAILABLE", result.getStatus());
		assertEquals("Book A", result.getBookName());
		assertEquals("Location A", result.getLocationName());
		assertEquals(1, result.getBorrowHistory().size());
		assertEquals("Student User", result.getBorrowHistory().get(0).getStudentName());
		assertEquals("STU001", result.getBorrowHistory().get(0).getStudentCode());
	}

	@Test
	void getBookCopyDetails_NotFound() {
		when(bookCopyRepository.findById(1L)).thenReturn(Optional.empty());

		AppException exception = assertThrows(AppException.class, () -> bookCopyService.getBookCopyDetails(1L));
		assertEquals(ErrorCode.BOOK_COPY_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	void updateCirculationStatus_Success() {
		BookCopy copy = BookCopy.builder().id(1L).status("AVAILABLE").build();
		BookCopyStatusUpdateRequest request = new BookCopyStatusUpdateRequest("DAMAGED", null);

		when(bookCopyRepository.findById(1L)).thenReturn(Optional.of(copy));

		bookCopyService.updateCirculationStatus(1L, request);

		assertEquals("DAMAGED", copy.getStatus());
		verify(bookCopyRepository).save(copy);
	}

	@Test
	void updateCirculationStatus_NotFound() {
		BookCopyStatusUpdateRequest request = new BookCopyStatusUpdateRequest("DAMAGED", null);
		when(bookCopyRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(AppException.class, () -> bookCopyService.updateCirculationStatus(1L, request));
	}

	@Test
	void deleteBookCopy_Success() {
		BookCopy copy = BookCopy.builder().id(1L).build();
		when(bookCopyRepository.findById(1L)).thenReturn(Optional.of(copy));

		bookCopyService.deleteBookCopy(1L);

		verify(bookCopyRepository).delete(copy);
	}

	@Test
	void deleteBookCopy_NotFound() {
		when(bookCopyRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(AppException.class, () -> bookCopyService.deleteBookCopy(1L));
	}
}
