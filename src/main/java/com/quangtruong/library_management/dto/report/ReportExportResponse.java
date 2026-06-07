package com.quangtruong.library_management.dto.report;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportExportResponse {
	String downloadUrl;
	String message;
}
