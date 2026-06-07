package com.quangtruong.library_management.dto.fine;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FineCreateRequest {
	@NotNull(message = "Student cannot be null")
	StudentIdRequest student;

	@NotNull(message = "Fine type cannot be null")
	String fineType;

	@NotNull(message = "Amount cannot be null")
	BigDecimal amount;

	LocalDateTime dueDate;
	String note;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class StudentIdRequest {
		UUID id;
	}
}
