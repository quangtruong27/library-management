package com.quangtruong.library_management.dto.faculty;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FacultyRequest {
	@NotBlank(message = "Tên khoa không được để trống")
	String name;
}
