package com.quangtruong.library_management.dto.report;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FineRevenueReportResponse {
	String periodLabel;
	BigDecimal totalFinesGenerated;
	BigDecimal totalFinesCollected;
}
