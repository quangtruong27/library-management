package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.fine.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IFineService {
	Page<FineDetailResponse> getAllFines(UUID studentId, String status, String fineType, Pageable pageable);

	FineDetailResponse getFineById(Long fineId);

	FineDetailResponse createFineManual(FineCreateRequest request);

	FineDetailResponse updateFine(Long fineId, FineUpdateRequest request);

	PaymentResponse recordPayment(Long fineId, PaymentRequest request, UUID staffUserId);

	Page<PaymentResponse> getAllPayments(UUID studentId, LocalDateTime startDate, LocalDateTime endDate, String paymentMethod, Pageable pageable);

	List<PaymentResponse> getPaymentHistoryForFine(Long fineId);

	void deleteFine(Long fineId);

	void sendPaymentReceipt(Long fineId);

	FineBalanceResponse getFineBalance(UUID studentId);
}
