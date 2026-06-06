package com.quangtruong.library_management.dto.role;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest {
	@NotBlank(message = "Tên vai trò không được để trống")
	String name;
	String description;

	// List of permission IDs
	Set<Long> permissions;
}
