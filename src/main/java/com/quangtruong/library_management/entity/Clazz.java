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
public class Clazz {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(nullable = false, unique = true)
	String name;

	@Column(name = "course_year")
	String courseYear;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "faculty_id")
	Faculty faculty;

}
