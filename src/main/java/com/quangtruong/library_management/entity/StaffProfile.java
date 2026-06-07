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
public class StaffProfile {

	// PK = FK to users.id
	@Id
	UUID id;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "id")
	User user;

	@Column(name = "employee_code",
			nullable = false,
			unique = true)
	String employeeCode;

	String phone;

	@Enumerated(EnumType.STRING)
	Gender gender;

	LocalDate dob;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "position_id")
	StaffPosition position;

	String address;

	@Column(name = "avatar_url")
	String avatarUrl;
}
