package com.quangtruong.library_management.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewRequest {

	@NotNull(message = "Rating không được để trống")
	@Min(value = 1, message = "Rating tối thiểu là 1 sao")
	@Max(value = 5, message = "Rating tối đa là 5 sao")
	Integer rating;

	String comment;
}
