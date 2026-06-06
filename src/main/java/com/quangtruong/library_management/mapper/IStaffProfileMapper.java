package com.quangtruong.library_management.mapper;

import com.quangtruong.library_management.dto.staff.StaffProfileRequest;
import com.quangtruong.library_management.dto.staff.StaffProfileResponse;
import com.quangtruong.library_management.entity.StaffProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
		componentModel = "spring",
		unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE
)
public interface IStaffProfileMapper {

	/*
	Create
	Request -> Entity
	 */
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "position", ignore = true)
	StaffProfile toEntity(StaffProfileRequest request);

	/*
	Entity -> Response
	 */
	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "user.name", target = "name")
	@Mapping(source = "user.email", target = "email")
	@Mapping(source = "user.status.name", target = "status")
	@Mapping(source = "position.name", target = "position")
	StaffProfileResponse toResponse(StaffProfile entity);

	/*
	Update
	 */
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "position", ignore = true)
	void update(
			@MappingTarget StaffProfile entity,
			StaffProfileRequest request
	);
}