package com.quangtruong.library_management.dto.returns;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReturnRequest {
	LoanIdRequest loan;
	String condition; // NORMAL, DAMAGED_LIGHT, DAMAGED_MEDIUM, DAMAGED_HEAVY, LOST
	String note;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class LoanIdRequest {
		Long id;
	}
}
