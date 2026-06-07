package com.quangtruong.library_management.dto.report;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportExportRequest {
	@NotBlank(message = "Report type is required")
	String reportType;

	@NotBlank(message = "Format is required")
	String format;
}
