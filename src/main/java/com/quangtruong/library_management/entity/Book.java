package com.quangtruong.library_management.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Book {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(nullable = false)
	String name;

	@Column(columnDefinition = "TEXT")
	String description;

	BigDecimal price; // price used to calculate fine

	@Column(name = "publisher_year")
	Integer publisherYear;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	Category category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "publisher_id")
	Publisher publisher;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "book_author", // Intermediate table name
			joinColumns = @JoinColumn(name = "book_id"), // Foreign key column referencing Book
			inverseJoinColumns = @JoinColumn(name = "author_id") // Foreign key column referencing Author
	)
	Set<Author> authors = new HashSet<>();
}
