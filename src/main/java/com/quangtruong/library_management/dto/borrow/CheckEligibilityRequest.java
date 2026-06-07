package com.quangtruong.library_management.dto.borrow;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckEligibilityRequest {
	@NotNull(message = "Student cannot be null")
	StudentIdRequest student;

	@NotNull(message = "Copy cannot be null")
	CopyIdRequest copy;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class StudentIdRequest {
		UUID id;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CopyIdRequest {
		Long id;
	}
}
