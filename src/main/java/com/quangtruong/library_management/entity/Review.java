package com.quangtruong.librarymanagement.entity;

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
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(nullable = false)
	Integer rating; // 1 → 5 sao

	@Column(columnDefinition = "TEXT")
	String comment;

	@Column(name = "created_at")
	LocalDateTime createdAt;

	@Column(name = "updated_at")
	LocalDateTime updatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id")
	Book book;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_profile_id")
	StudentProfile studentProfile;
}
