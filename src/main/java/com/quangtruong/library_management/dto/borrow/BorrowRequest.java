package com.quangtruong.library_management.dto.borrow;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowRequest {
	@NotNull(message = "Student cannot be null")
	StudentIdRequest student;

	CopyIdRequest copy;         // For direct borrowing
	ReservationIdRequest reservation;  // For borrowing from reservation

	LocalDateTime dueDate;
	String note;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class StudentIdRequest {
		UUID id;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CopyIdRequest {
		Long id;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ReservationIdRequest {
		Long id;
	}
}
