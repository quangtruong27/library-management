package com.quangtruong.library_management.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Borrow {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "borrow_date")
	LocalDateTime borrowDate;

	@Column(name = "due_date")
	LocalDateTime dueDate;

	String status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_profile_id")
	StudentProfile studentProfile;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "staff_profile_id")
	StaffProfile staffProfile;

	@OneToMany(mappedBy = "borrow", cascade = CascadeType.ALL)
	List<BorrowDetail> borrowDetai = new ArrayList<>();
}
