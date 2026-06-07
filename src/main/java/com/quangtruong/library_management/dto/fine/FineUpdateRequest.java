package com.quangtruong.library_management.dto.fine;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FineUpdateRequest {
	BigDecimal amount;
	String status;
	String note;
	LocalDateTime dueDate;
}
