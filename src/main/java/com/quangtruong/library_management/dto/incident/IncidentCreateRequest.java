package com.quangtruong.library_management.dto.incident;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IncidentCreateRequest {
	@NotBlank(message = "Title is required")
	String title;

	@NotBlank(message = "Description is required")
	String description;

	@NotBlank(message = "Priority is required")
	String priority;
}
