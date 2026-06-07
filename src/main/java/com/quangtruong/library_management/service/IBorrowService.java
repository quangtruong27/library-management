package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.borrow.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface IBorrowService {

	//Get borrow session info
	BorrowSessionResponse getBorrowSession(UUID studentId);

	// Confirm student successfully picked up reserved book.
	BorrowResponse confirmReservationPickup(Long reservationId, UUID staffUserId);

	//Cancel reservation directly at the counter
	void cancelReservationAtCounter(Long reservationId);

	// Check if the student is eligible to borrow a specific book copy
	CheckEligibilityResponse checkEligibility(CheckEligibilityRequest request);

	//Create new borrow record at counter.
	BorrowResponse createBorrow(BorrowRequest request, UUID staffUserId);

	//Confirm librarian delivered physical book to student
	BorrowResponse confirmLoanPickup(Long loanId);

	//Search and retrieve list of borrow records
	Page<BorrowResponse> getAllBorrows(UUID studentId, String status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

	// View detailed info of a specific borrow record.
	BorrowResponse getBorrowById(Long id);

	// Cancel or remove borrow record before delivering book
	void deleteBorrow(Long id);

	// Mark book as lost or damaged during borrowing.
	BorrowResponse markIssue(Long id, String issueType);
}
