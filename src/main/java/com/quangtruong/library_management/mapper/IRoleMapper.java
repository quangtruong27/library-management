package com.quangtruong.library_management.mapper;

import com.quangtruong.library_management.dto.role.RoleRequest;
import com.quangtruong.library_management.dto.role.RoleResponse;
import com.quangtruong.library_management.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
		componentModel = "spring",
		unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IRoleMapper {

	// Ignore mapping permissions from request, let Service fetch from database
	@Mapping(target = "permissions", ignore = true)
	Role toEntity(RoleRequest request);

	RoleResponse toResponse(Role entity);

	@Mapping(target = "permissions", ignore = true)
	void update(@MappingTarget Role entity, RoleRequest request);
}
