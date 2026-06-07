package com.quangtruong.library_management.dto.bookcopy;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookCopyStatusUpdateRequest {

	@NotBlank(message = "Trạng thái không được để trống")
	String status;

	String note;
}
