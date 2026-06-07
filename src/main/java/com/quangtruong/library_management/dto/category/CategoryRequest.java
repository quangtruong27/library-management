package com.quangtruong.library_management.dto.category;

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
public class CategoryRequest {

	@NotBlank(message = "Tên category không được để trống")
	@Size(max = 255, message = "Tên category tối đa 255 ký tự")
	String name;

	@Size(max = 1000, message = "Mô tả tối đa 1000 ký tự")
	String description;
}
