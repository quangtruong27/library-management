package com.quangtruong.library_management.dto.borrow;

/*
Used to display each history entry inside the View Book Copy Detail function
 */

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowHistoryResponse {
	Long borrowId;
	LocalDateTime borrowDate;
	LocalDateTime dueDate;
	LocalDateTime returnDate;
	String studentName;
	String studentCode;
	String note;
}
