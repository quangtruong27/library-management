package com.quangtruong.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
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
public class Fine {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "fine_type")
	String fineType;

	@Column(name = "reference_price")
	BigDecimal referencePrice;

	String status;

	@Column(name = "created_date")
	java.time.LocalDateTime createdDate;

	@Column(name = "due_date")
	java.time.LocalDateTime dueDate;

	String note;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_profile_id")
	StudentProfile studentProfile;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "borrow_detail_id")
	BorrowDetail borrowDetail;

	@OneToMany(mappedBy = "fine")
	List<PaymentDetail> paymentDetail = new ArrayList<>();
}
