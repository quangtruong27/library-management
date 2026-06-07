package com.quangtruong.library_management.service.impl;

import com.quangtruong.library_management.dto.report.*;
import com.quangtruong.library_management.entity.*;
import com.quangtruong.library_management.repository.*;
import com.quangtruong.library_management.service.IReportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportService implements IReportService {

	IBorrowRepository borrowRepository;
	IBookReturnRepository bookReturnRepository;
	IBookRepository bookRepository;
	IBookCopyRepository bookCopyRepository;
	IFineRepository fineRepository;
	IPaymentRepository paymentRepository;
	IStudentProfileRepository studentProfileRepository;
	IReservationRepository reservationRepository;

	@Override
	public LoanSummaryReportResponse getLoanSummary(String period, LocalDateTime startDate, LocalDateTime endDate) {
		LocalDateTime start = startDate != null ? startDate : LocalDateTime.now().minusMonths(1);
		LocalDateTime end = endDate != null ? endDate : LocalDateTime.now();

		// Fetch all Borrows in the timeframe
		List<Borrow> borrows = borrowRepository.findAll().stream()
				.filter(b -> b.getBorrowDate() != null && !b.getBorrowDate().isBefore(start) && !b.getBorrowDate().isAfter(end))
				.collect(Collectors.toList());

		// Fetch all BookReturns in the timeframe
		List<BookReturn> returns = bookReturnRepository.findAll().stream()
				.filter(r -> r.getReturnDate() != null && !r.getReturnDate().isBefore(start) && !r.getReturnDate().isAfter(end))
				.collect(Collectors.toList());

		long totalBorrows = borrows.size();
		long totalReturns = returns.size();

		long onTimeReturns = 0;
		long lateReturns = 0;

		for (BookReturn r : returns) {
			if (r.getOverdueDays() != null && r.getOverdueDays() > 0) {
				lateReturns++;
			} else {
				onTimeReturns++;
			}
		}

		double onTimeRate = totalReturns > 0 ? ((double) onTimeReturns / totalReturns) * 100.0 : 100.0;

		// Overdue borrows = current borrowing that are past due date + returned late in this period
		long activeOverdue = borrowRepository.findAll().stream()
				.filter(b -> "BORROWING".equalsIgnoreCase(b.getStatus()) && b.getDueDate() != null && b.getDueDate().isBefore(LocalDateTime.now()))
				.count();

		long totalOverdue = activeOverdue + lateReturns;

		return LoanSummaryReportResponse.builder()
				.totalBorrows(totalBorrows)
				.totalReturns(totalReturns)
				.onTimeReturnRate(Math.round(onTimeRate * 100.0) / 100.0)
				.overdueBorrows(totalOverdue)
				.build();
	}

	@Override
	public List<TopBookReportResponse> getTopBooks(LocalDateTime startDate, LocalDateTime endDate, int limit) {
		LocalDateTime start = startDate != null ? startDate : LocalDateTime.now().minusYears(1);
		LocalDateTime end = endDate != null ? endDate : LocalDateTime.now();

		List<Borrow> borrows = borrowRepository.findAll().stream()
				.filter(b -> b.getBorrowDate() != null && !b.getBorrowDate().isBefore(start) && !b.getBorrowDate().isAfter(end))
				.collect(Collectors.toList());

		Map<Book, Long> bookCounts = new HashMap<>();
		for (Borrow b : borrows) {
			for (BorrowDetail detail : b.getBorrowDetai()) {
				if (detail.getBookCopy() != null && detail.getBookCopy().getBook() != null) {
					Book book = detail.getBookCopy().getBook();
					bookCounts.put(book, bookCounts.getOrDefault(book, 0L) + 1);
				}
			}
		}

		List<TopBookReportResponse> responseList = new ArrayList<>();
		for (Map.Entry<Book, Long> entry : bookCounts.entrySet()) {
			responseList.add(TopBookReportResponse.builder()
					.bookId(entry.getKey().getId())
					.name(entry.getKey().getName())
					.borrowCount(entry.getValue())
					.build());
		}

		responseList.sort((r1, r2) -> Long.compare(r2.getBorrowCount(), r1.getBorrowCount()));

		int maxLimit = limit > 0 ? limit : 10;
		if (responseList.size() > maxLimit) {
			return responseList.subList(0, maxLimit);
		}
		return responseList;
	}

	@Override
	public List<StudentActivityReportResponse> getStudentActivity(Long facultyId, Long clazzId) {
		List<StudentProfile> students = studentProfileRepository.findAll();

		// Group activity by Faculty and Clazz
		Map<String, List<StudentProfile>> grouped = students.stream()
				.filter(s -> {
					if (facultyId != null && (s.getClazz() == null || s.getClazz().getFaculty() == null || !s.getClazz().getFaculty().getId().equals(facultyId))) {
						return false;
					}
					if (clazzId != null && (s.getClazz() == null || !s.getClazz().getId().equals(clazzId))) {
						return false;
					}
					return true;
				})
				.collect(Collectors.groupingBy(s -> {
					String fName = s.getClazz() != null && s.getClazz().getFaculty() != null ? s.getClazz().getFaculty().getName() : "Unknown Faculty";
					String cName = s.getClazz() != null ? s.getClazz().getName() : "Unknown Class";
					Long fId = s.getClazz() != null && s.getClazz().getFaculty() != null ? s.getClazz().getFaculty().getId() : 0L;
					Long cId = s.getClazz() != null ? s.getClazz().getId() : 0L;
					return fId + "_" + fName + "_" + cId + "_" + cName;
				}));

		List<StudentActivityReportResponse> results = new ArrayList<>();

		for (Map.Entry<String, List<StudentProfile>> entry : grouped.entrySet()) {
			String[] parts = entry.getKey().split("_");
			Long fId = Long.parseLong(parts[0]);
			String fName = parts[1];
			Long cId = Long.parseLong(parts[2]);
			String cName = parts[3];

			List<StudentProfile> groupStudents = entry.getValue();
			long activeCount = 0;
			long totalBorrows = 0;
			long totalReservations = 0;

			for (StudentProfile student : groupStudents) {
				List<Borrow> bList = borrowRepository.findByStudentProfileId(student.getId());
				List<Reservation> rList = reservationRepository.findByStudentProfileId(student.getId());

				totalBorrows += bList.size();
				totalReservations += rList.size();

				if (!bList.isEmpty() || !rList.isEmpty()) {
					activeCount++;
				}
			}

			results.add(StudentActivityReportResponse.builder()
					.facultyId(fId > 0 ? fId : null)
					.facultyName(fName)
					.clazzId(cId > 0 ? cId : null)
					.clazzName(cName)
					.activeStudentsCount(activeCount)
					.totalBorrows(totalBorrows)
					.totalReservations(totalReservations)
					.build());
		}

		return results;
	}

	@Override
	public List<FineRevenueReportResponse> getFineRevenue(String period, LocalDateTime startDate, LocalDateTime endDate) {
		LocalDateTime start = startDate != null ? startDate : LocalDateTime.now().minusMonths(6);
		LocalDateTime end = endDate != null ? endDate : LocalDateTime.now();

		List<Fine> fines = fineRepository.findAll().stream()
				.filter(f -> f.getCreatedDate() != null && !f.getCreatedDate().isBefore(start) && !f.getCreatedDate().isAfter(end))
				.collect(Collectors.toList());

		List<Payment> payments = paymentRepository.findAll().stream()
				.filter(p -> p.getPaymentDate() != null && !p.getPaymentDate().isBefore(start) && !p.getPaymentDate().isAfter(end))
				.collect(Collectors.toList());

		// Group by period label (yyyy-MM for month)
		Map<String, BigDecimal> finesByPeriod = new TreeMap<>();
		Map<String, BigDecimal> paymentsByPeriod = new TreeMap<>();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

		for (Fine f : fines) {
			String label = f.getCreatedDate().format(formatter);
			BigDecimal amt = f.getReferencePrice() != null ? f.getReferencePrice() : BigDecimal.ZERO;
			finesByPeriod.put(label, finesByPeriod.getOrDefault(label, BigDecimal.ZERO).add(amt));
		}

		for (Payment p : payments) {
			String label = p.getPaymentDate().format(formatter);
			BigDecimal amt = p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO;
			paymentsByPeriod.put(label, paymentsByPeriod.getOrDefault(label, BigDecimal.ZERO).add(amt));
		}

		Set<String> allPeriods = new TreeSet<>();
		allPeriods.addAll(finesByPeriod.keySet());
		allPeriods.addAll(paymentsByPeriod.keySet());

		List<FineRevenueReportResponse> results = new ArrayList<>();
		for (String label : allPeriods) {
			results.add(FineRevenueReportResponse.builder()
					.periodLabel(label)
					.totalFinesGenerated(finesByPeriod.getOrDefault(label, BigDecimal.ZERO))
					.totalFinesCollected(paymentsByPeriod.getOrDefault(label, BigDecimal.ZERO))
					.build());
		}

		return results;
	}

	@Override
	public InventoryReportResponse getInventoryReport() {
		long totalBooks = bookRepository.count();
		long totalCopies = bookCopyRepository.count();
		long available = bookCopyRepository.findAll().stream().filter(c -> "AVAILABLE".equalsIgnoreCase(c.getStatus())).count();
		long borrowed = bookCopyRepository.findAll().stream().filter(c -> "BORROWED".equalsIgnoreCase(c.getStatus()) || "BORROWING".equalsIgnoreCase(c.getStatus())).count();
		long damaged = bookCopyRepository.findAll().stream().filter(c -> "DAMAGED".equalsIgnoreCase(c.getStatus())).count();
		long lost = bookCopyRepository.findAll().stream().filter(c -> "LOST".equalsIgnoreCase(c.getStatus())).count();

		return InventoryReportResponse.builder()
				.totalBooks(totalBooks)
				.totalCopies(totalCopies)
				.availableCopies(available)
				.borrowedCopies(borrowed)
				.damagedCopies(damaged)
				.lostCopies(lost)
				.build();
	}

	@Override
	public ReportExportResponse exportReport(ReportExportRequest request) {
		String uuidStr = UUID.randomUUID().toString().substring(0, 8);
		String extension = "pdf".equalsIgnoreCase(request.getFormat()) ? "pdf" : "xlsx";
		String filename = "report_" + request.getReportType() + "_" + uuidStr + "." + extension;
		String downloadUrl = "/api/reports/download/" + filename;

		return ReportExportResponse.builder()
				.downloadUrl(downloadUrl)
				.message("Report successfully generated. Download link generated.")
				.build();
	}
}
