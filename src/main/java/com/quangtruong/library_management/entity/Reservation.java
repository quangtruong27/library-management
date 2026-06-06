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
public class Reservation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "reservation_date")
	LocalDateTime reservationDate;

	@Column(name = "expired_date")
	LocalDateTime expiredDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status_id")
	ReservationStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_profile_id")
	StudentProfile studentProfile;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_copy_id")
	BookCopy bookCopy;
}
