package com.quangtruong.library_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	UUID id;

	@Column(nullable = false)
	String name;

	@Column(nullable = false)
	String password;

	@Column(unique = true, nullable = false)
	String email;

	// Account status
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status_id", nullable = false)
	UserStatus status;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "user_role", // Intermediate table name
			joinColumns = @JoinColumn(name = "user_id"), // Foreign key column referencing User
			inverseJoinColumns = @JoinColumn(name = "role_id") // Foreign key column referencing Role
	)
	Set<Role> roles = new HashSet<>();

	// Cascade helps automatically save Profile when saving User if Profile is present
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
			@JsonIgnore
	StudentProfile studentProfile;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
			@JsonIgnore
	StaffProfile staffProfile;
}
