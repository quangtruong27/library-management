package com.quangtruong.library_management.dto.fine;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FineResponse {
	Long id;
	String fineType;
	BigDecimal referencePrice;
	String status;
	Long borrowDetailId;
}
