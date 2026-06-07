package com.quangtruong.library_management.service.impl;

import com.quangtruong.library_management.dto.fine.*;
import com.quangtruong.library_management.entity.*;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.repository.*;
import com.quangtruong.library_management.service.IFineService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FineService implements IFineService {

	IFineRepository fineRepository;
	IPaymentRepository paymentRepository;
	IPaymentDetailRepository paymentDetailRepository;
	IStudentProfileRepository studentProfileRepository;
	IStaffProfileRepository staffProfileRepository;

	private FineDetailResponse mapToDetailResponse(Fine fine) {
		Long borrowDetailId = fine.getBorrowDetail() != null ? fine.getBorrowDetail().getId() : null;
		Long loanId = fine.getBorrowDetail() != null && fine.getBorrowDetail().getBorrow() != null 
				? fine.getBorrowDetail().getBorrow().getId() 
				: null;

		return FineDetailResponse.builder()
				.id(fine.getId())
				.fineType(fine.getFineType())
				.amount(fine.getReferencePrice())
				.status(fine.getStatus())
				.createdDate(fine.getCreatedDate())
				.dueDate(fine.getDueDate())
				.note(fine.getNote())
				.studentId(fine.getStudentProfile().getId())
				.studentName(fine.getStudentProfile().getUser().getName())
				.studentCode(fine.getStudentProfile().getStudentCode())
				.borrowDetailId(borrowDetailId)
				.loanId(loanId)
				.build();
	}

	private PaymentResponse mapToPaymentResponse(Payment payment) {
		List<PaymentResponse.PaymentDetailAllocResponse> details = payment.getDetails().stream()
				.map(d -> PaymentResponse.PaymentDetailAllocResponse.builder()
						.fineId(d.getFine().getId())
						.amountAllocated(d.getAmountAllocated())
						.fineType(d.getFine().getFineType())
						.build())
				.collect(Collectors.toList());

		return PaymentResponse.builder()
				.id(payment.getId())
				.paymentMethod(payment.getPaymentMethod())
				.paymentDate(payment.getPaymentDate())
				.amount(payment.getAmount())
				.studentId(payment.getStudentProfile().getId())
				.studentName(payment.getStudentProfile().getUser().getName())
				.staffName(payment.getStaffProfile() != null ? payment.getStaffProfile().getUser().getName() : "Hệ thống")
				.details(details)
				.build();
	}

	@Override
	public Page<FineDetailResponse> getAllFines(UUID studentId, String status, String fineType, Pageable pageable) {
		return fineRepository.searchFines(studentId, status, fineType, pageable)
				.map(this::mapToDetailResponse);
	}

	@Override
	public FineDetailResponse getFineById(Long fineId) {
		Fine fine = fineRepository.findById(fineId)
				.orElseThrow(() -> new AppException(ErrorCode.FINE_NOT_FOUND));
		return mapToDetailResponse(fine);
	}

	@Override
	@Transactional
	public FineDetailResponse createFineManual(FineCreateRequest request) {
		UUID studentId = request.getStudent() != null ? request.getStudent().getId() : null;
		if (studentId == null) {
			throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
		}

		StudentProfile student = studentProfileRepository.findById(studentId)
				.orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

		Fine fine = Fine.builder()
				.fineType(request.getFineType())
				.referencePrice(request.getAmount())
				.status("UNPAID")
				.createdDate(LocalDateTime.now())
				.dueDate(request.getDueDate() != null ? request.getDueDate() : LocalDateTime.now().plusDays(7))
				.note(request.getNote())
				.studentProfile(student)
				.build();

		fineRepository.save(fine);
		return mapToDetailResponse(fine);
	}

	@Override
	@Transactional
	public FineDetailResponse updateFine(Long fineId, FineUpdateRequest request) {
		Fine fine = fineRepository.findById(fineId)
				.orElseThrow(() -> new AppException(ErrorCode.FINE_NOT_FOUND));

		if (request.getAmount() != null) {
			fine.setReferencePrice(request.getAmount());
		}
		if (request.getStatus() != null) {
			fine.setStatus(request.getStatus());
		}
		if (request.getNote() != null) {
			fine.setNote(request.getNote());
		}
		if (request.getDueDate() != null) {
			fine.setDueDate(request.getDueDate());
		}

		fineRepository.save(fine);
		return mapToDetailResponse(fine);
	}

	@Override
	@Transactional
	public PaymentResponse recordPayment(Long fineId, PaymentRequest request, UUID staffUserId) {
		Fine fine = fineRepository.findById(fineId)
				.orElseThrow(() -> new AppException(ErrorCode.FINE_NOT_FOUND));

		StaffProfile staff = staffProfileRepository.findById(staffUserId)
				.orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));

		// Record Payment
		Payment payment = Payment.builder()
				.paymentMethod(request.getPaymentMethod())
				.paymentDate(LocalDateTime.now())
				.amount(request.getAmount())
				.studentProfile(fine.getStudentProfile())
				.staffProfile(staff)
				.details(new ArrayList<>())
				.build();

		paymentRepository.save(payment);

		// Calculate the total amount paid for this fine
		BigDecimal alreadyPaid = paymentDetailRepository.findByFineId(fineId).stream()
				.map(PaymentDetail::getAmountAllocated)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal remaining = fine.getReferencePrice().subtract(alreadyPaid);
		BigDecimal allocated = request.getAmount().min(remaining);

		PaymentDetail detail = PaymentDetail.builder()
				.amountAllocated(allocated)
				.payment(payment)
				.fine(fine)
				.build();

		paymentDetailRepository.save(detail);
		payment.getDetails().add(detail);

		// Update Fine status
		BigDecimal totalPaid = alreadyPaid.add(allocated);
		if (totalPaid.compareTo(fine.getReferencePrice()) >= 0) {
			fine.setStatus("PAID");
		}
		fineRepository.save(fine);

		return mapToPaymentResponse(payment);
	}

	@Override
	public Page<PaymentResponse> getAllPayments(UUID studentId, LocalDateTime startDate, LocalDateTime endDate, String paymentMethod, Pageable pageable) {
		return paymentRepository.searchPayments(studentId, startDate, endDate, paymentMethod, pageable)
				.map(this::mapToPaymentResponse);
	}

	@Override
	public List<PaymentResponse> getPaymentHistoryForFine(Long fineId) {
		List<PaymentDetail> details = paymentDetailRepository.findByFineId(fineId);
		return details.stream()
				.map(PaymentDetail::getPayment)
				.distinct()
				.map(this::mapToPaymentResponse)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void deleteFine(Long fineId) {
		Fine fine = fineRepository.findById(fineId)
				.orElseThrow(() -> new AppException(ErrorCode.FINE_NOT_FOUND));
		fineRepository.delete(fine);
	}

	@Override
	public void sendPaymentReceipt(Long fineId) {
		log.info("Sending payment receipt email for fine ID: {}", fineId);
		// Simulate sending email successfully
	}

	@Override
	public FineBalanceResponse getFineBalance(UUID studentId) {
		StudentProfile student = studentProfileRepository.findById(studentId)
				.orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

		List<Fine> unpaidFines = fineRepository.findByStudentProfileIdAndStatus(studentId, "UNPAID");

		BigDecimal totalBalance = BigDecimal.ZERO;
		for (Fine fine : unpaidFines) {
			BigDecimal alreadyPaid = paymentDetailRepository.findByFineId(fine.getId()).stream()
					.map(PaymentDetail::getAmountAllocated)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal remaining = fine.getReferencePrice().subtract(alreadyPaid);
			if (remaining.compareTo(BigDecimal.ZERO) > 0) {
				totalBalance = totalBalance.add(remaining);
			}
		}

		return FineBalanceResponse.builder()
				.studentId(studentId)
				.studentName(student.getUser().getName())
				.studentCode(student.getStudentCode())
				.balance(totalBalance)
				.build();
	}
}
