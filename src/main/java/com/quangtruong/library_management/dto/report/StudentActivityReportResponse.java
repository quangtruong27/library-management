package com.quangtruong.library_management.dto.report;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentActivityReportResponse {
	Long facultyId;
	String facultyName;
	Long clazzId;
	String clazzName;
	long activeStudentsCount;
	long totalBorrows;
	long totalReservations;
}
