package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.returns.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface IReturnService {
	ReturnScanResponse scanReturn(ReturnScanRequest request);

	ConditionCheckResponse checkCondition(Long loanId, ConditionCheckRequest request);

	BookReturnResponse createIssue(Long loanId, IssueCreateRequest request, UUID staffUserId);

	BookReturnResponse updateFine(Long loanId, FineUpdateRequest request);

	BookReturnResponse confirmReturn(ReturnRequest request, UUID staffUserId);

	void sendReceipt(Long loanId);

	Page<BookReturnResponse> getAllReturns(UUID studentId, String status, LocalDateTime startDate, LocalDateTime endDate, Boolean hasViolation, Pageable pageable);

	BookReturnResponse getReturnById(Long id);
}
