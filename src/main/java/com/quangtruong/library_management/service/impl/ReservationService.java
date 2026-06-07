package com.quangtruong.library_management.service.impl;

import com.quangtruong.library_management.dto.reservation.ReservationRequest;
import com.quangtruong.library_management.dto.reservation.ReservationResponse;
import com.quangtruong.library_management.entity.BookCopy;
import com.quangtruong.library_management.entity.Reservation;
import com.quangtruong.library_management.entity.ReservationStatus;
import com.quangtruong.library_management.entity.StudentProfile;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.mapper.IReservationMapper;
import com.quangtruong.library_management.repository.IBookCopyRepository;
import com.quangtruong.library_management.repository.IReservationRepository;
import com.quangtruong.library_management.repository.IReservationStatusRepository;
import com.quangtruong.library_management.repository.IStudentProfileRepository;
import com.quangtruong.library_management.service.IReservationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReservationService implements IReservationService {

	static final int MAX_BORROW_LIMIT = 3;
	static final List<String> ACTIVE_STATUS_NAMES = List.of("PENDING", "READY");

	IReservationRepository       reservationRepository;
	IReservationStatusRepository reservationStatusRepository;
	IStudentProfileRepository    studentProfileRepository;
	IBookCopyRepository          bookCopyRepository;
	IReservationMapper           reservationMapper;

	// =======================  HELPER  =======================

	private ReservationStatus getStatus(String name) {
		return reservationStatusRepository.findByName(name)
				.orElseThrow(() -> new AppException(ErrorCode.RESERVATION_STATUS_NOT_FOUND));
	}

	// Calculate working days — skip Saturday and Sunday
	private LocalDate addWorkingDays(LocalDate start, int days) {
		LocalDate result = start;
		int added = 0;
		while (added < days) {
			result = result.plusDays(1);
			if (result.getDayOfWeek() != DayOfWeek.SATURDAY
					&& result.getDayOfWeek() != DayOfWeek.SUNDAY) {
				added++;
			}
		}
		return result;
	}

	// =====================  MAIN METHODS  ===================

	@Override
	public Page<ReservationResponse> getAll(Pageable pageable) {
		return reservationRepository.findAll(pageable)
				.map(reservationMapper::toResponse);
	}

	@Override
	public Page<ReservationResponse> getMyReservations(UUID userId, Pageable pageable) {

		return reservationRepository
				.findByStudentProfileId(userId, pageable)
				.map(reservationMapper::toResponse);
	}

	@Override
	public ReservationResponse getById(Long id) {

		Reservation reservation = reservationRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.RESERVATION_NOT_FOUND));

		return reservationMapper.toResponse(reservation);
	}

	@Override
	@Transactional
	public ReservationResponse create(ReservationRequest request, UUID userId) {

		// 1. Find student
		StudentProfile student = studentProfileRepository.findById(userId)
				.orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

		// 2. Check borrow limit
		long activeCount = reservationRepository
				.countByStudentProfileIdAndStatusNameIn(userId, ACTIVE_STATUS_NAMES);
		if (activeCount >= MAX_BORROW_LIMIT) {
			throw new AppException(ErrorCode.RESERVATION_LIMIT_EXCEEDED);
		}

		// 3. Find AVAILABLE BookCopy
		BookCopy copy;
		Long copyId = request.getCopy() != null ? request.getCopy().getId() : null;
		Long bookId = request.getBook() != null ? request.getBook().getId() : null;
		if (copyId != null) {
			copy = bookCopyRepository.findById(copyId)
					.orElseThrow(() -> new AppException(ErrorCode.BOOK_COPY_NOT_FOUND));
			if (!"AVAILABLE".equals(copy.getStatus())) {
				throw new AppException(ErrorCode.NO_COPY_AVAILABLE);
			}
		} else {
			if (bookId == null) {
				throw new AppException(ErrorCode.BOOK_NOT_FOUND);
			}
			copy = bookCopyRepository.findByBookId(bookId)
					.stream()
					.filter(c -> "AVAILABLE".equals(c.getStatus()))
					.findFirst()
					.orElseThrow(() -> new AppException(ErrorCode.NO_COPY_AVAILABLE));
		}

		// 4. Create Reservation
		Reservation reservation = new Reservation();
		reservation.setStudentProfile(student);
		reservation.setBookCopy(copy);
		reservation.setStatus(getStatus("PENDING"));
		reservation.setReservationDate(LocalDateTime.now());
		reservation.setExpiredDate(
				addWorkingDays(LocalDate.now(), 3).atTime(23, 59, 59));

		// 5. Change BookCopy status → RESERVED
		copy.setStatus("RESERVED");
		bookCopyRepository.save(copy);

		return reservationMapper.toResponse(reservationRepository.save(reservation));
	}

	@Override
	@Transactional
	public void cancel(Long id, UUID userId) {
		Reservation reservation = reservationRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.RESERVATION_NOT_FOUND));

		// Check ownership
		if (!reservation.getStudentProfile().getId().equals(userId)) {
			throw new AppException(ErrorCode.RESERVATION_NOT_OWNED);
		}

		// Can only cancel if PENDING or READY
		String currentStatus = reservation.getStatus().getName();
		if (!"PENDING".equals(currentStatus) && !"READY".equals(currentStatus)) {
			throw new AppException(ErrorCode.RESERVATION_ALREADY_CANCELLED);
		}

		reservation.setStatus(getStatus("CANCELLED"));
		reservationRepository.save(reservation);

		// Release BookCopy
		BookCopy copy = reservation.getBookCopy();
		copy.setStatus("AVAILABLE");
		bookCopyRepository.save(copy);
	}

	@Override
	public ReservationResponse updateStatus(Long id, String statusName) {
		Reservation reservation = reservationRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.RESERVATION_NOT_FOUND));
		reservation.setStatus(getStatus(statusName));
		return reservationMapper.toResponse(reservationRepository.save(reservation));
	}

	@Override
	@Scheduled(cron = "0 0 * * * *") // Run every hour
	@Transactional
	public void processExpiredReservations() {
		List<Reservation> expired = reservationRepository
				.findAllByStatusNameAndExpiredDateBefore("PENDING", LocalDateTime.now());

		ReservationStatus expiredStatus = getStatus("EXPIRED");
		expired.forEach(r -> {
			r.setStatus(expiredStatus);
			r.getBookCopy().setStatus("AVAILABLE");
			bookCopyRepository.save(r.getBookCopy());
		});
		reservationRepository.saveAll(expired);
	}
}
