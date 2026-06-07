package com.quangtruong.library_management.dto.incident;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IncidentCommentResponse {
	Long id;
	UUID userId;
	String userName;
	String content;
	LocalDateTime createdDate;
}
