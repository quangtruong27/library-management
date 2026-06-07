package com.quangtruong.library_management.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class StudentProfile {
	@Id
	UUID id;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "id")
	User user;

	@Column(name = "student_code",
			nullable = false,
			unique = true)
	String studentCode;

	String phone;

	@Enumerated(EnumType.STRING)
	Gender gender;

	LocalDate dob;

	String address;

	@Column(name = "enrollment_year")
	Integer enrollmentYear;

	@Column(name = "avatar_url")
	String avatarUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "clazz_id")
	Clazz clazz;
}
