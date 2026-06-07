package com.quangtruong.library_management.dto.fine;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentRequest {
	@NotNull(message = "Payment method cannot be null")
	String paymentMethod; // CASH, BANK_TRANSFER

	@NotNull(message = "Amount cannot be null")
	BigDecimal amount;
}
