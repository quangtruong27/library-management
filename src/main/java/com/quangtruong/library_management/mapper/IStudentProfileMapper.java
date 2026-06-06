package com.quangtruong.library_management.mapper;

import com.quangtruong.library_management.dto.student.StudentProfileRequest;
import com.quangtruong.library_management.dto.student.StudentProfileResponse;
import com.quangtruong.library_management.entity.StudentProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
		componentModel = "spring",
		unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IStudentProfileMapper {
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "clazz", ignore = true)
	StudentProfile toEntity(StudentProfileRequest request);

	@Mapping(source = "user.name", target = "name")
	@Mapping(source = "user.email", target = "email")
	@Mapping(source = "user.status.id", target = "statusId")
	@Mapping(source = "user.status.name", target = "statusName")
	@Mapping(source = "clazz.id", target = "clazzId")
	@Mapping(source = "clazz.name", target = "clazzName")
	StudentProfileResponse toResponse(StudentProfile entity);

	@Mapping(target = "user", ignore = true)
	@Mapping(target = "clazz", ignore = true)
	void update(@MappingTarget StudentProfile entity, StudentProfileRequest request);
}
