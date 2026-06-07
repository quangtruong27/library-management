package com.quangtruong.library_management.dto.booklocation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookLocationRequest {
	@NotBlank(message = "Tên kệ không được để trống")
	@Size(max = 255, message = "Tên kệ tối đa 255 ký tự")
	String name;
	@Size(max = 255, message = "Mô tả tối đa 255 ký tự")
	String description;
}
