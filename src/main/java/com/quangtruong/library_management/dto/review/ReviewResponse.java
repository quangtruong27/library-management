package com.quangtruong.library_management.dto.review;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewResponse {
	Long id;
	Integer rating;
	String comment;
	LocalDateTime createdAt;
	LocalDateTime updatedAt;

	Long bookId;
	String bookName;

	String studentCode;
	String studentName;
}
