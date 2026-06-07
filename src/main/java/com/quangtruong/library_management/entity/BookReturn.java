package com.quangtruong.library_management.entity;

import jakarta.persistence.*;
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
@Entity
@Table(name = "book_return")
public class BookReturn {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "return_date", nullable = false)
	LocalDateTime returnDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "borrow_detail_id", nullable = false)
	BorrowDetail borrowDetail;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "staff_profile_id", nullable = false)
	StaffProfile staffProfile;

	@Column(nullable = false)
	String status;

	@Column(name = "book_condition", nullable = false)
	String bookCondition;

	@Column(name = "fine_amount")
	BigDecimal fineAmount;

	@Column(name = "overdue_days")
	Integer overdueDays;

	@Column(name = "overdue_fine")
	BigDecimal overdueFine;

	@Column(name = "damage_fine")
	BigDecimal damageFine;

	String note;
}
