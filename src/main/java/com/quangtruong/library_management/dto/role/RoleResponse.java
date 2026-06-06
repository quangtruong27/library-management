package com.quangtruong.library_management.dto.role;

import com.quangtruong.library_management.dto.permission.PermissionResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse {
	Long id;
	String name;
	String description;
	Set<PermissionResponse> permissions;
}
