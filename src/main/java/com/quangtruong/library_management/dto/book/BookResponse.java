package com.quangtruong.library_management.dto.book;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookResponse {
	Long id;

	String name;

	String description;

	BigDecimal price;

	Integer publisherYear;

	// Category
	Long categoryId;
	String categoryName;

	// Publisher
	Long publisherId;
	String publisherName;

	// Authors
	Set<Long> authorId;
	Set<String> authorName;

	Integer totalCopies;
	Integer availableCopies;
	List<BookCopyResponse> copies;

}
