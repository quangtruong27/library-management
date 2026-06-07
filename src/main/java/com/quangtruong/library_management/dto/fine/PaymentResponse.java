package com.quangtruong.library_management.dto.fine;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {
	Long id;
	String paymentMethod;
	LocalDateTime paymentDate;
	BigDecimal amount;
	UUID studentId;
	String studentName;
	String staffName;
	List<PaymentDetailAllocResponse> details;

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PaymentDetailAllocResponse {
		Long fineId;
		BigDecimal amountAllocated;
		String fineType;
	}
}
