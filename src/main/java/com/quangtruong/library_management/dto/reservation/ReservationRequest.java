package com.quangtruong.library_management.dto.reservation;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReservationRequest {
	@NotNull(message = "Id không được để trống")
	BookIdRequest book;

	// specific copy
	CopyIdRequest copy;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class BookIdRequest {
		Long id;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CopyIdRequest {
		Long id;
	}
}
