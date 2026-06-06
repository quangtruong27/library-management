package com.quangtruong.library_management.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class PaymentDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	// how much money allocated to pay the fine
	@Column(name = "amount_allocated")
	BigDecimal amountAllocated;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_id")
	Payment payment;

	// thanh toans cho khoan phat nao
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fine_id")
	Fine fine;
}
