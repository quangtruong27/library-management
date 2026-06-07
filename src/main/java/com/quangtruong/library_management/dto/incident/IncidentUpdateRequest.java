package com.quangtruong.library_management.dto.incident;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IncidentUpdateRequest {
	String status;
	AssigneeRequest assignee;
	String resolutionNote;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AssigneeRequest {
		UUID id;
	}
}
