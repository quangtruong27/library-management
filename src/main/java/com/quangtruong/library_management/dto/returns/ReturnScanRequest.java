package com.quangtruong.library_management.dto.returns;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReturnScanRequest {
	StudentIdRequest student;
	CopyIdRequest copy;
	LoanIdRequest loan;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class StudentIdRequest {
		UUID id;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CopyIdRequest {
		Long id;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class LoanIdRequest {
		Long id;
	}
}
