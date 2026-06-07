package com.quangtruong.library_management.dto.returns;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookReturnResponse {
	Long id;
	LocalDateTime returnDate;
	Long loanId;
	Long copyId;
	String bookName;
	UUID studentId;
	String studentName;
	String studentCode;
	String staffName;
	String status;
	String bookCondition;
	BigDecimal fineAmount;
	Integer overdueDays;
	BigDecimal overdueFine;
	BigDecimal damageFine;
	String note;
}
