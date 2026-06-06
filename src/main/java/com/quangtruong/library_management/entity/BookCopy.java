package com.quangtruong.library_management.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class BookCopy {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(unique = true, nullable = false)
	String qrCode;
	String status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id")
	Book book;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "booklocation_id")
	BookLocation bookLocation;
}
