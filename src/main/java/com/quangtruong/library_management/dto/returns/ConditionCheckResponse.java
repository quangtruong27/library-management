package com.quangtruong.library_management.dto.returns;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConditionCheckResponse {
	Long loanId;
	String condition;
	BigDecimal estimatedDamageFine;
	BigDecimal overdueFine;
	BigDecimal totalEstimatedFine;
	String note;
}
