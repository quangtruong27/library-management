package com.quangtruong.library_management.dto.borrow;

import com.quangtruong.library_management.dto.fine.FineResponse;
import com.quangtruong.library_management.dto.reservation.ReservationResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowSessionResponse {
	UUID studentId;
	String studentName;
	String studentCode;
	List<ReservationResponse> activeReservations;
	List<BorrowResponse> activeBorrows;
	List<FineResponse> unpaidFines;
	BigDecimal unpaidFinesTotal;
	int borrowLimit;
	long currentBorrowCount; // reservation + borrow
}
