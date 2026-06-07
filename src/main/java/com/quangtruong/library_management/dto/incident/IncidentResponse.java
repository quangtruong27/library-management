package com.quangtruong.library_management.dto.incident;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IncidentResponse {
	Long id;
	String title;
	String description;
	String priority;
	String status;
	UUID reporterId;
	String reporterName;
	UUID assigneeId;
	String assigneeName;
	String resolutionNote;
	LocalDateTime createdDate;
	LocalDateTime updatedDate;
	List<IncidentCommentResponse> comments;
}
