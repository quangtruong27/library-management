package com.quangtruong.library_management.dto.incident;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IncidentCommentRequest {
	@NotBlank(message = "Comment content cannot be empty")
	String content;
}
