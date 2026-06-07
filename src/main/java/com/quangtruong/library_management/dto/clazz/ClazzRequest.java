package com.quangtruong.library_management.dto.clazz;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClazzRequest {
	@NotBlank(message = "Tên lớp không được để trống")
	String name;

	String courseYear; // Course year

	Long facultyId; // Faculty ID
}
