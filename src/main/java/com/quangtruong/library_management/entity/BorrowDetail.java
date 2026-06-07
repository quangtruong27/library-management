package com.quangtruong.library_management.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class BorrowDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "return_date")
	LocalDateTime returnDate;
	String note;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "borrow_id")
	Borrow borrow;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_copy_id")
	BookCopy bookCopy;
}
