package com.quangtruong.library_management.dto.fine;

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
public class FineDetailResponse {
	Long id;
	String fineType;
	BigDecimal amount;
	String status;
	LocalDateTime createdDate;
	LocalDateTime dueDate;
	String note;
	UUID studentId;
	String studentName;
	String studentCode;
	Long borrowDetailId;
	Long loanId;
}
