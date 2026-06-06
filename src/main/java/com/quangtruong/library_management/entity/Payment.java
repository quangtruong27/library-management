package com.quangtruong.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Payment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "payment_method")
	String paymentMethod;

	@Column(name = "payment_date")
	java.time.LocalDateTime paymentDate;

	java.math.BigDecimal amount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_profile_id")
	StudentProfile studentProfile;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "staff_profile_id")
	StaffProfile staffProfile;

	@OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
	List<PaymentDetail> details = new ArrayList<>();
}
