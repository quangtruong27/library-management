package com.quangtruong.library_management.dto.reservation;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReservationStatusRequest {

	@NotBlank(message = "Status không được để trống")
	String status; // Status name: "PENDING", "READY", "EXPIRED", "CANCELLED"
}
