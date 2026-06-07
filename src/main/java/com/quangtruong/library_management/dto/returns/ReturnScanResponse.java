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
public class ReturnScanResponse {
	Long loanId;
	UUID studentId;
	String studentName;
	String studentCode;
	Long copyId;
	String bookName;
	LocalDateTime borrowDate;
	LocalDateTime dueDate;
	Long overdueDays;
	BigDecimal estimatedOverdueFine;
	String status;
}
