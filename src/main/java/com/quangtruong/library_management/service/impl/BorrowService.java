package com.quangtruong.library_management.service.impl;

import com.quangtruong.library_management.dto.borrow.*;
import com.quangtruong.library_management.entity.*;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.mapper.IBorrowMapper;
import com.quangtruong.library_management.mapper.IFineMapper;
import com.quangtruong.library_management.mapper.IReservationMapper;
import com.quangtruong.library_management.repository.*;
import com.quangtruong.library_management.service.IBorrowService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BorrowService implements IBorrowService {

	static final int MAX_BORROW_LIMIT = 3;
	static final List<String> ACTIVE_RESERVATION_STATUSES = List.of("PENDING", "READY");

	IBorrowRepository borrowRepository;
	IBorrowDetailRepository borrowDetailRepository;
	IStudentProfileRepository studentProfileRepository;
	IStaffProfileRepository staffProfileRepository;
	IBookCopyRepository bookCopyRepository;
	IReservationRepository reservationRepository;
	IReservationStatusRepository reservationStatusRepository;
	IFineRepository fineRepository;
	IBorrowMapper borrowMapper;
	IReservationMapper reservationMapper;
	IFineMapper fineMapper;

	private ReservationStatus getReservationStatus(String name) {
		return reservationStatusRepository.findByName(name)
				.orElseThrow(() -> new AppException(ErrorCode.RESERVATION_STATUS_NOT_FOUND));
	}

	@Override
	public BorrowSessionResponse getBorrowSession(UUID studentId) {
		StudentProfile student = studentProfileRepository.findById(studentId)
				.orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

		// Active reservations
		List<Reservation> activeReservations = reservationRepository
				.findByStudentProfileIdAndStatusNameIn(studentId, ACTIVE_RESERVATION_STATUSES);

		//Active borrow
		List<Borrow> activeBorrows = borrowRepository
				.findByStudentProfileIdAndStatus(studentId, "BORROWING");

			// Unpaid fine
		List<Fine> unpaidFines = fineRepository
				.findByStudentProfileIdAndStatus(studentId, "UNPAID");

		BigDecimal totalFines = unpaidFines.stream()
				.map(f -> f.getReferencePrice() != null ? f.getReferencePrice() : BigDecimal.ZERO)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		long currentBorrowCount = activeReservations.size() + activeBorrows.size();

		return BorrowSessionResponse.builder()
				.studentId(student.getId())
				.studentName(student.getUser().getName())
				.studentCode(student.getStudentCode())
				.activeReservations(activeReservations.stream()
						.map(reservationMapper::toResponse)
						.collect(Collectors.toList()))
				.activeBorrows(activeBorrows.stream()
						.map(borrowMapper::toResponse)
						.collect(Collectors.toList()))
				.unpaidFines(unpaidFines.stream()
						.map(fineMapper::toResponse)
						.collect(Collectors.toList()))
				.unpaidFinesTotal(totalFines)
				.borrowLimit(MAX_BORROW_LIMIT)
				.currentBorrowCount(currentBorrowCount)
				.build();
	}

	@Override
	@Transactional
	public BorrowResponse confirmReservationPickup(Long reservationId, UUID staffUserId) {
		Reservation reservation = reservationRepository.findById(reservationId)
				.orElseThrow(() -> new AppException(ErrorCode.RESERVATION_NOT_FOUND));

		String resStatus = reservation.getStatus().getName();
		if (!"PENDING".equals(resStatus) && !"READY".equals(resStatus)) {
			throw new AppException(ErrorCode.RESERVATION_ALREADY_CANCELLED);
		}

		StaffProfile staff = staffProfileRepository.findById(staffUserId)
				.orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));

		// Update reservation status to COMPLETED
		reservation.setStatus(getReservationStatus("COMPLETED"));
		reservationRepository.save(reservation);

		// Update book copy status to BORROWED
		BookCopy copy = reservation.getBookCopy();
		copy.setStatus("BORROWED");
		bookCopyRepository.save(copy);

		// Create borrow record
		Borrow borrow = new Borrow();
		borrow.setStudentProfile(reservation.getStudentProfile());
		borrow.setStaffProfile(staff);
		borrow.setBorrowDate(LocalDateTime.now());
		borrow.setDueDate(LocalDateTime.now().plusDays(14));
		borrow.setStatus("BORROWING");
		borrow.setBorrowDetai(new ArrayList<>());

		BorrowDetail detail = new BorrowDetail();
		detail.setBorrow(borrow);
		detail.setBookCopy(copy);
		detail.setNote("Mượn từ đơn đặt trước #" + reservationId);

		borrow.getBorrowDetai().add(detail);

		Borrow savedBorrow = borrowRepository.save(borrow);
		return borrowMapper.toResponse(savedBorrow);
	}

	@Override
	@Transactional
	public void cancelReservationAtCounter(Long reservationId) {
		Reservation reservation = reservationRepository.findById(reservationId)
				.orElseThrow(() -> new AppException(ErrorCode.RESERVATION_NOT_FOUND));

		String resStatus = reservation.getStatus().getName();
		if (!"PENDING".equals(resStatus) && !"READY".equals(resStatus)) {
			throw new AppException(ErrorCode.RESERVATION_ALREADY_CANCELLED);
		}

		reservation.setStatus(getReservationStatus("CANCELLED"));
		reservationRepository.save(reservation);

		BookCopy copy = reservation.getBookCopy();
		copy.setStatus("AVAILABLE");
		bookCopyRepository.save(copy);
	}

	@Override
	public CheckEligibilityResponse checkEligibility(CheckEligibilityRequest request) {
		List<String> reasons = new ArrayList<>();
		boolean eligible = true;

		UUID studentId = request.getStudent() != null ? request.getStudent().getId() : null;
		Long copyId = request.getCopy() != null ? request.getCopy().getId() : null;
		if (studentId == null) {
			throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
		}
		if (copyId == null) {
			throw new AppException(ErrorCode.BOOK_COPY_NOT_FOUND);
		}

		StudentProfile student = studentProfileRepository.findById(studentId)
				.orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

		BookCopy copy = bookCopyRepository.findById(copyId)
				.orElseThrow(() -> new AppException(ErrorCode.BOOK_COPY_NOT_FOUND));

		// 1. Book copy status
		if (!"AVAILABLE".equals(copy.getStatus())) {
			eligible = false;
			reasons.add("Bản in không sẵn sàng để mượn (trạng thái: " + copy.getStatus() + ")");
		}

		// 2. Borrow limit
		long activeResCount = reservationRepository
				.countByStudentProfileIdAndStatusNameIn(studentId, ACTIVE_RESERVATION_STATUSES);
		long activeBorrowCount = borrowRepository
				.findByStudentProfileIdAndStatus(studentId, "BORROWING").size();

		if (activeResCount + activeBorrowCount >= MAX_BORROW_LIMIT) {
			eligible = false;
			reasons.add("Sinh viên đã đạt giới hạn mượn sách tối đa (" + MAX_BORROW_LIMIT + " cuốn)");
		}

		// 3. Unpaid fines
		boolean hasUnpaidFines = fineRepository
				.existsByStudentProfileIdAndStatus(studentId, "UNPAID");
		if (hasUnpaidFines) {
			eligible = false;
			reasons.add("Sinh viên có phí phạt chưa thanh toán");
		}

		return CheckEligibilityResponse.builder()
				.eligible(eligible)
				.reasons(reasons)
				.build();
	}

	@Override
	@Transactional
	public BorrowResponse createBorrow(BorrowRequest request, UUID staffUserId) {
		StaffProfile staff = staffProfileRepository.findById(staffUserId)
				.orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));

		Long reservationId = request.getReservation() != null ? request.getReservation().getId() : null;
		Long copyId = request.getCopy() != null ? request.getCopy().getId() : null;
		UUID studentId = request.getStudent() != null ? request.getStudent().getId() : null;

		// Case 1: Borrowing from reservation
		if (reservationId != null) {
			return confirmReservationPickup(reservationId, staffUserId);
		}

		// Case 2: Direct borrowing (free borrow)
		if (copyId == null) {
			throw new AppException(ErrorCode.BOOK_COPY_NOT_FOUND);
		}
		if (studentId == null) {
			throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
		}

		// Check eligibility
		CheckEligibilityRequest eligibilityRequest = CheckEligibilityRequest.builder()
				.student(new CheckEligibilityRequest.StudentIdRequest(studentId))
				.copy(new CheckEligibilityRequest.CopyIdRequest(copyId))
				.build();
		CheckEligibilityResponse eligibility = checkEligibility(eligibilityRequest);

		if (!eligibility.isEligible()) {
			// Find appropriate error code
			if (eligibility.getReasons().stream().anyMatch(r -> r.contains("giới hạn"))) {
				throw new AppException(ErrorCode.BORROW_LIMIT_EXCEEDED);
			} else if (eligibility.getReasons().stream().anyMatch(r -> r.contains("phí phạt"))) {
				throw new AppException(ErrorCode.STUDENT_HAS_UNPAID_FINES);
			} else {
				throw new AppException(ErrorCode.BOOK_COPY_NOT_AVAILABLE);
			}
		}

		StudentProfile student = studentProfileRepository.findById(studentId)
				.orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

		BookCopy copy = bookCopyRepository.findById(copyId)
				.orElseThrow(() -> new AppException(ErrorCode.BOOK_COPY_NOT_FOUND));

		// Set copy status to RESERVED when borrow is created with PENDING status
		copy.setStatus("RESERVED");
		bookCopyRepository.save(copy);

		Borrow borrow = new Borrow();
		borrow.setStudentProfile(student);
		borrow.setStaffProfile(staff);
		borrow.setBorrowDate(LocalDateTime.now());
		borrow.setDueDate(request.getDueDate() != null ? request.getDueDate() : LocalDateTime.now().plusDays(14));
		borrow.setStatus("PENDING");
		borrow.setBorrowDetai(new ArrayList<>());

		BorrowDetail detail = new BorrowDetail();
		detail.setBorrow(borrow);
		detail.setBookCopy(copy);
		detail.setNote(request.getNote());

		borrow.getBorrowDetai().add(detail);

		Borrow savedBorrow = borrowRepository.save(borrow);
		return borrowMapper.toResponse(savedBorrow);
	}

	@Override
	@Transactional
	public BorrowResponse confirmLoanPickup(Long loanId) {
		Borrow borrow = borrowRepository.findById(loanId)
				.orElseThrow(() -> new AppException(ErrorCode.BORROW_NOT_FOUND));

		if (!"PENDING".equals(borrow.getStatus())) {
			throw new AppException(ErrorCode.INVALID_LOAN_STATUS);
		}

		borrow.setStatus("BORROWING");
		borrow.setBorrowDate(LocalDateTime.now());
		borrow.setDueDate(borrow.getBorrowDate().plusDays(14));

		for (BorrowDetail detail : borrow.getBorrowDetai()) {
			BookCopy copy = detail.getBookCopy();
			copy.setStatus("BORROWED");
			bookCopyRepository.save(copy);
		}

		Borrow saved = borrowRepository.save(borrow);
		return borrowMapper.toResponse(saved);
	}

	@Override
	public Page<BorrowResponse> getAllBorrows(UUID studentId, String status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
		return borrowRepository.searchBorrows(studentId, status, startDate, endDate, pageable)
				.map(borrowMapper::toResponse);
	}

	@Override
	public BorrowResponse getBorrowById(Long id) {
		Borrow borrow = borrowRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.BORROW_NOT_FOUND));
		return borrowMapper.toResponse(borrow);
	}

	@Override
	@Transactional
	public void deleteBorrow(Long id) {
		Borrow borrow = borrowRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.BORROW_NOT_FOUND));

		if (!"PENDING".equals(borrow.getStatus())) {
			throw new AppException(ErrorCode.BORROW_CANNOT_CANCEL);
		}

		borrow.setStatus("CANCELLED");
		for (BorrowDetail detail : borrow.getBorrowDetai()) {
			BookCopy copy = detail.getBookCopy();
			copy.setStatus("AVAILABLE");
			bookCopyRepository.save(copy);
		}

		borrowRepository.save(borrow);
	}

	@Override
	@Transactional
	public BorrowResponse markIssue(Long id, String issueType) {
		if (!"LOST".equalsIgnoreCase(issueType) && !"DAMAGED".equalsIgnoreCase(issueType)) {
			throw new AppException(ErrorCode.INVALID_ISSUE_TYPE);
		}

		Borrow borrow = borrowRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.BORROW_NOT_FOUND));

		if (!"BORROWING".equals(borrow.getStatus())) {
			throw new AppException(ErrorCode.INVALID_LOAN_STATUS);
		}

		String finalStatus = issueType.toUpperCase();
		borrow.setStatus(finalStatus);

		for (BorrowDetail detail : borrow.getBorrowDetai()) {
			BookCopy copy = detail.getBookCopy();
			copy.setStatus(finalStatus);
			bookCopyRepository.save(copy);

			// Automatically create a fine record
			BigDecimal price = copy.getBook().getPrice();
			if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
				price = BigDecimal.valueOf(100000); //Fallback default if book price is not set
			}

			Fine fine = Fine.builder()
					.fineType(finalStatus)
					.referencePrice(price)
					.status("UNPAID")
					.studentProfile(borrow.getStudentProfile())
					.borrowDetail(detail)
					.build();

			fineRepository.save(fine);
		}

		Borrow saved = borrowRepository.save(borrow);
		return borrowMapper.toResponse(saved);
	}
}
