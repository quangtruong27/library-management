package com.quangtruong.library_management.dto.reservation;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReservationResponse {
	Long id;
	LocalDateTime reservationDate;
	LocalDateTime expiredDate;

	// Status information (from reservation_status table)
	Long statusId;
	String statusName;

	// Book copy information
	Long bookCopyId;
	String bookName;   // Book.name (not title)
	String bookQrCode;

	// Student information
	String studentCode;
	String studentName;
}
