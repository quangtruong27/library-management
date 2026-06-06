package com.quangtruong.librarymanagement.entity;

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
public class Incident {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(nullable = false)
	String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	String description;

	@Column(nullable = false)
	String priority;

	@Column(nullable = false)
	String status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reporter_id", nullable = false)
	User reporter;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "assignee_id")
	User assignee;

	@Column(name = "resolution_note", length = 1000)
	String resolutionNote;

	@Column(name = "created_date", nullable = false)
	LocalDateTime createdDate;

	@Column(name = "updated_date", nullable = false)
	LocalDateTime updatedDate;

	@OneToMany(mappedBy = "incident", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	List<IncidentComment> comments = new ArrayList<>();
}
