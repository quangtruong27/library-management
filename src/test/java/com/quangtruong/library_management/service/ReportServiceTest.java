package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.report.*;
import com.quangtruong.library_management.entity.*;
import com.quangtruong.library_management.repository.*;
import com.quangtruong.library_management.service.impl.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ReportServiceTest {

	@Mock
	IBorrowRepository borrowRepository;
	@Mock
	IBookReturnRepository bookReturnRepository;
	@Mock
	IBookRepository bookRepository;
	@Mock
	IBookCopyRepository bookCopyRepository;
	@Mock
	IFineRepository fineRepository;
	@Mock
	IPaymentRepository paymentRepository;
	@Mock
	IStudentProfileRepository studentProfileRepository;
	@Mock
	IReservationRepository reservationRepository;

	@InjectMocks
	ReportService reportService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void getInventoryReport_Success() {
		when(bookRepository.count()).thenReturn(5L);
		when(bookCopyRepository.count()).thenReturn(20L);

		List<BookCopy> copies = List.of(
				BookCopy.builder().status("AVAILABLE").build(),
				BookCopy.builder().status("AVAILABLE").build(),
				BookCopy.builder().status("BORROWED").build(),
				BookCopy.builder().status("LOST").build(),
				BookCopy.builder().status("DAMAGED").build()
		);
		when(bookCopyRepository.findAll()).thenReturn(copies);

		InventoryReportResponse response = reportService.getInventoryReport();

		assertNotNull(response);
		assertEquals(5L, response.getTotalBooks());
		assertEquals(20L, response.getTotalCopies());
		assertEquals(2L, response.getAvailableCopies());
		assertEquals(1L, response.getBorrowedCopies());
		assertEquals(1L, response.getDamagedCopies());
		assertEquals(1L, response.getLostCopies());
	}

	@Test
	void exportReport_Success() {
		ReportExportRequest request = ReportExportRequest.builder()
				.reportType("inventory")
				.format("pdf")
				.build();

		ReportExportResponse response = reportService.exportReport(request);

		assertNotNull(response);
		assertTrue(response.getDownloadUrl().contains("report_inventory"));
		assertTrue(response.getDownloadUrl().endsWith(".pdf"));
	}

	@Test
	void getTopBooks_Success() {
		Book book1 = Book.builder().id(1L).name("Clean Code").build();
		BookCopy copy1 = BookCopy.builder().id(1L).book(book1).build();
		BorrowDetail detail1 = BorrowDetail.builder().bookCopy(copy1).build();

		Book book2 = Book.builder().id(2L).name("Refactoring").build();
		BookCopy copy2 = BookCopy.builder().id(2L).book(book2).build();
		BorrowDetail detail2 = BorrowDetail.builder().bookCopy(copy2).build();

		Borrow borrow1 = Borrow.builder()
				.borrowDate(LocalDateTime.now().minusDays(2))
				.borrowDetai(List.of(detail1, detail2))
				.build();

		Borrow borrow2 = Borrow.builder()
				.borrowDate(LocalDateTime.now().minusDays(1))
				.borrowDetai(List.of(detail1))
				.build();

		when(borrowRepository.findAll()).thenReturn(List.of(borrow1, borrow2));

		List<TopBookReportResponse> response = reportService.getTopBooks(null, null, 5);

		assertNotNull(response);
		assertEquals(2, response.size());
		assertEquals("Clean Code", response.get(0).getName());
		assertEquals(2L, response.get(0).getBorrowCount()); // Clean Code borrowed twice
		assertEquals("Refactoring", response.get(1).getName());
		assertEquals(1L, response.get(1).getBorrowCount());
	}
}
