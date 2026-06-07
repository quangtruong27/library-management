package com.quangtruong.library_management.service.impl;

import com.quangtruong.library_management.dto.returns.*;
import com.quangtruong.library_management.entity.*;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.repository.*;
import com.quangtruong.library_management.service.IReturnService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReturnService implements IReturnService {

	static final BigDecimal OVERDUE_DAILY_RATE = BigDecimal.valueOf(5000L); // 5,000 VND / overdue day

	IBorrowRepository borrowRepository;
	IBorrowDetailRepository borrowDetailRepository;
	IBookCopyRepository bookCopyRepository;
	IStudentProfileRepository studentProfileRepository;
	IStaffProfileRepository staffProfileRepository;
	IFineRepository fineRepository;
	IBookReturnRepository bookReturnRepository;

	private int calculateOverdueDays(LocalDateTime dueDate, LocalDateTime returnDate) {
		if (returnDate.isAfter(dueDate)) {
			long days = ChronoUnit.DAYS.between(dueDate.toLocalDate(), returnDate.toLocalDate());
			return days > 0 ? (int) days : 0;
		}
		return 0;
	}

	private BigDecimal calculateOverdueFine(int overdueDays) {
		return OVERDUE_DAILY_RATE.multiply(BigDecimal.valueOf(overdueDays));
	}

	private BigDecimal calculateDamageFine(String condition, BigDecimal bookPrice) {
		BigDecimal basePrice = bookPrice != null && bookPrice.compareTo(BigDecimal.ZERO) > 0 
				? bookPrice 
				: BigDecimal.valueOf(100000); // default 100k

		if ("DAMAGED_LIGHT".equalsIgnoreCase(condition)) {
			return basePrice.multiply(BigDecimal.valueOf(0.1)); // 10%
		} else if ("DAMAGED_MEDIUM".equalsIgnoreCase(condition)) {
			return basePrice.multiply(BigDecimal.valueOf(0.3)); // 30%
		} else if ("DAMAGED_HEAVY".equalsIgnoreCase(condition)) {
			return basePrice.multiply(BigDecimal.valueOf(0.5)); // 50%
		} else if ("LOST".equalsIgnoreCase(condition)) {
			return basePrice; // 100%
		}
		return BigDecimal.ZERO;
	}

	private BookReturnResponse mapToResponse(BookReturn bookReturn) {
		BorrowDetail detail = bookReturn.getBorrowDetail();
		Borrow borrow = detail.getBorrow();
		return BookReturnResponse.builder()
				.id(bookReturn.getId())
				.returnDate(bookReturn.getReturnDate())
				.loanId(borrow.getId())
				.copyId(detail.getBookCopy().getId())
				.bookName(detail.getBookCopy().getBook().getName())
				.studentId(borrow.getStudentProfile().getId())
				.studentName(borrow.getStudentProfile().getUser().getName())
				.studentCode(borrow.getStudentProfile().getStudentCode())
				.staffName(bookReturn.getStaffProfile().getUser().getName())
				.status(bookReturn.getStatus())
				.bookCondition(bookReturn.getBookCondition())
				.fineAmount(bookReturn.getFineAmount())
				.overdueDays(bookReturn.getOverdueDays())
				.overdueFine(bookReturn.getOverdueFine())
				.damageFine(bookReturn.getDamageFine())
				.note(bookReturn.getNote())
				.build();
	}

	@Override
	public ReturnScanResponse scanReturn(ReturnScanRequest request) {
		UUID studentId = request.getStudent() != null ? request.getStudent().getId() : null;
		Long copyId = request.getCopy() != null ? request.getCopy().getId() : null;
		Long loanId = request.getLoan() != null ? request.getLoan().getId() : null;

		BorrowDetail borrowDetail = null;

		if (loanId != null) {
			Borrow borrow = borrowRepository.findById(loanId)
					.orElseThrow(() -> new AppException(ErrorCode.BORROW_NOT_FOUND));
			if (!borrow.getBorrowDetai().isEmpty()) {
				borrowDetail = borrow.getBorrowDetai().get(0);
			}
		} else if (copyId != null) {
			// Find the active borrow detail for this book copy
			List<Borrow> activeBorrows = borrowRepository.findByStudentProfileIdAndStatus(studentId, "BORROWING");
			for (Borrow borrow : activeBorrows) {
				for (BorrowDetail detail : borrow.getBorrowDetai()) {
					if (detail.getBookCopy().getId().equals(copyId)) {
						borrowDetail = detail;
						break;
					}
				}
				if (borrowDetail != null) break;
			}
		}

		if (borrowDetail == null) {
			throw new AppException(ErrorCode.BORROW_NOT_FOUND);
		}

		Borrow borrow = borrowDetail.getBorrow();
		LocalDateTime now = LocalDateTime.now();
		int overdueDays = calculateOverdueDays(borrow.getDueDate(), now);
		BigDecimal overdueFine = calculateOverdueFine(overdueDays);

		return ReturnScanResponse.builder()
				.loanId(borrow.getId())
				.studentId(borrow.getStudentProfile().getId())
				.studentName(borrow.getStudentProfile().getUser().getName())
				.studentCode(borrow.getStudentProfile().getStudentCode())
				.copyId(borrowDetail.getBookCopy().getId())
				.bookName(borrowDetail.getBookCopy().getBook().getName())
				.borrowDate(borrow.getBorrowDate())
				.dueDate(borrow.getDueDate())
				.overdueDays((long) overdueDays)
				.estimatedOverdueFine(overdueFine)
				.status(borrow.getStatus())
				.build();
	}

	@Override
	public ConditionCheckResponse checkCondition(Long loanId, ConditionCheckRequest request) {
		Borrow borrow = borrowRepository.findById(loanId)
				.orElseThrow(() -> new AppException(ErrorCode.BORROW_NOT_FOUND));

		if (borrow.getBorrowDetai().isEmpty()) {
			throw new AppException(ErrorCode.BORROW_NOT_FOUND);
		}

		BorrowDetail detail = borrow.getBorrowDetai().get(0);
		BigDecimal bookPrice = detail.getBookCopy().getBook().getPrice();

		int overdueDays = calculateOverdueDays(borrow.getDueDate(), LocalDateTime.now());
		BigDecimal overdueFine = calculateOverdueFine(overdueDays);
		BigDecimal damageFine = calculateDamageFine(request.getCondition(), bookPrice);

		return ConditionCheckResponse.builder()
				.loanId(loanId)
				.condition(request.getCondition())
				.estimatedDamageFine(damageFine)
				.overdueFine(overdueFine)
				.totalEstimatedFine(overdueFine.add(damageFine))
				.note(request.getNote())
				.build();
	}

	@Override
	@Transactional
	public BookReturnResponse createIssue(Long loanId, IssueCreateRequest request, UUID staffUserId) {
		Borrow borrow = borrowRepository.findById(loanId)
				.orElseThrow(() -> new AppException(ErrorCode.BORROW_NOT_FOUND));

		if (borrow.getBorrowDetai().isEmpty()) {
			throw new AppException(ErrorCode.BORROW_NOT_FOUND);
		}

		BorrowDetail detail = borrow.getBorrowDetai().get(0);
		StaffProfile staff = staffProfileRepository.findById(staffUserId)
				.orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));

		int overdueDays = calculateOverdueDays(borrow.getDueDate(), LocalDateTime.now());

		// Check if BookReturn already exists
		BookReturn bookReturn = bookReturnRepository.findByBorrowDetailId(detail.getId()).orElse(null);
		if (bookReturn == null) {
			bookReturn = BookReturn.builder()
					.returnDate(LocalDateTime.now())
					.borrowDetail(detail)
					.staffProfile(staff)
					.status("PENDING_FINE")
					.bookCondition(request.getDamageFine().compareTo(BigDecimal.ZERO) > 0 ? "DAMAGED" : "NORMAL")
					.fineAmount(request.getDamageFine().add(request.getOverdueFine()))
					.overdueDays(overdueDays)
					.overdueFine(request.getOverdueFine())
					.damageFine(request.getDamageFine())
					.note(request.getNote())
					.build();
		} else {
			bookReturn.setFineAmount(request.getDamageFine().add(request.getOverdueFine()));
			bookReturn.setOverdueFine(request.getOverdueFine());
			bookReturn.setDamageFine(request.getDamageFine());
			bookReturn.setNote(request.getNote());
			bookReturn.setStatus("PENDING_FINE");
		}

		bookReturnRepository.save(bookReturn);

		// Create fine record in the system
		if (request.getOverdueFine().compareTo(BigDecimal.ZERO) > 0) {
			Fine ovFine = Fine.builder()
					.fineType("OVERDUE")
					.referencePrice(request.getOverdueFine())
					.status("UNPAID")
					.studentProfile(borrow.getStudentProfile())
					.borrowDetail(detail)
					.build();
			fineRepository.save(ovFine);
		}

		if (request.getDamageFine().compareTo(BigDecimal.ZERO) > 0) {
			Fine dmFine = Fine.builder()
					.fineType("DAMAGE")
					.referencePrice(request.getDamageFine())
					.status("UNPAID")
					.studentProfile(borrow.getStudentProfile())
					.borrowDetail(detail)
					.build();
			fineRepository.save(dmFine);
		}

		return mapToResponse(bookReturn);
	}

	@Override
	@Transactional
	public BookReturnResponse updateFine(Long loanId, FineUpdateRequest request) {
		BookReturn bookReturn = bookReturnRepository.findByBorrowDetailBorrowId(loanId)
				.orElseThrow(() -> new AppException(ErrorCode.BORROW_NOT_FOUND));

		bookReturn.setFineAmount(request.getFineAmount());
		bookReturnRepository.save(bookReturn);

		return mapToResponse(bookReturn);
	}

	@Override
	@Transactional
	public BookReturnResponse confirmReturn(ReturnRequest request, UUID staffUserId) {
		Long loanId = request.getLoan() != null ? request.getLoan().getId() : null;
		if (loanId == null) {
			throw new AppException(ErrorCode.BORROW_NOT_FOUND);
		}

		Borrow borrow = borrowRepository.findById(loanId)
				.orElseThrow(() -> new AppException(ErrorCode.BORROW_NOT_FOUND));

		if (borrow.getBorrowDetai().isEmpty()) {
			throw new AppException(ErrorCode.BORROW_NOT_FOUND);
		}

		BorrowDetail detail = borrow.getBorrowDetai().get(0);
		StaffProfile staff = staffProfileRepository.findById(staffUserId)
				.orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));

		BookReturn bookReturn = bookReturnRepository.findByBorrowDetailId(detail.getId()).orElse(null);

		LocalDateTime now = LocalDateTime.now();
		int overdueDays = calculateOverdueDays(borrow.getDueDate(), now);
		BigDecimal overdueFine = calculateOverdueFine(overdueDays);
		BigDecimal damageFine = calculateDamageFine(request.getCondition(), detail.getBookCopy().getBook().getPrice());

		if (bookReturn == null) {
			bookReturn = BookReturn.builder()
					.returnDate(now)
					.borrowDetail(detail)
					.staffProfile(staff)
					.status("RETURNED")
					.bookCondition(request.getCondition())
					.fineAmount(overdueFine.add(damageFine))
					.overdueDays(overdueDays)
					.overdueFine(overdueFine)
					.damageFine(damageFine)
					.note(request.getNote())
					.build();
		} else {
			bookReturn.setStatus("RETURNED");
			bookReturn.setBookCondition(request.getCondition());
			bookReturn.setNote(request.getNote());
			bookReturn.setReturnDate(now);
		}

		// Create actual Fine if overdue fine is incurred and not saved in the issues flow
		if (overdueFine.compareTo(BigDecimal.ZERO) > 0) {
			boolean exists = fineRepository.findByStudentProfileIdAndStatus(borrow.getStudentProfile().getId(), "UNPAID")
					.stream().anyMatch(f -> "OVERDUE".equals(f.getFineType()) && f.getBorrowDetail().getId().equals(detail.getId()));
			if (!exists) {
				Fine ovFine = Fine.builder()
						.fineType("OVERDUE")
						.referencePrice(overdueFine)
						.status("UNPAID")
						.studentProfile(borrow.getStudentProfile())
						.borrowDetail(detail)
						.build();
				fineRepository.save(ovFine);
			}
		}

		// Update book status and related records
		BookCopy copy = detail.getBookCopy();
		if ("NORMAL".equalsIgnoreCase(request.getCondition())) {
			copy.setStatus("AVAILABLE");
		} else if ("LOST".equalsIgnoreCase(request.getCondition())) {
			copy.setStatus("LOST");
		} else {
			copy.setStatus("DAMAGED");
		}
		bookCopyRepository.save(copy);

		detail.setReturnDate(now);
		borrowDetailRepository.save(detail);

		borrow.setStatus("RETURNED");
		borrowRepository.save(borrow);

		bookReturnRepository.save(bookReturn);

		return mapToResponse(bookReturn);
	}

	@Override
	public void sendReceipt(Long loanId) {
		log.info("Sending return receipt email for loan ID: {}", loanId);
		// Simulate sending email successfully
	}

	@Override
	public Page<BookReturnResponse> getAllReturns(UUID studentId, String status, LocalDateTime startDate, LocalDateTime endDate, Boolean hasViolation, Pageable pageable) {
		return bookReturnRepository.searchReturns(studentId, status, startDate, endDate, hasViolation, pageable)
				.map(this::mapToResponse);
	}

	@Override
	public BookReturnResponse getReturnById(Long id) {
		BookReturn bookReturn = bookReturnRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.BORROW_NOT_FOUND));
		return mapToResponse(bookReturn);
	}
}
