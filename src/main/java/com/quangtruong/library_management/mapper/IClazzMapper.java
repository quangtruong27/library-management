package com.quangtruong.library_management.mapper;

import com.quangtruong.library_management.dto.clazz.ClazzRequest;
import com.quangtruong.library_management.dto.clazz.ClazzResponse;
import com.quangtruong.library_management.entity.Clazz;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
		componentModel = "spring",
		unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IClazzMapper {
	// Ignore mapping faculty from request, service will fetch it from DB
	@Mapping(target = "faculty", ignore = true)
	Clazz toEntity(ClazzRequest request);

	// Get Faculty ID and Name to return for user viewing
	@Mapping(source = "faculty.id", target = "facultyId")
	@Mapping(source = "faculty.name", target = "facultyName")
	ClazzResponse toResponse(Clazz entity);

	@Mapping(target = "faculty", ignore = true)
	void update(@MappingTarget Clazz entity, ClazzRequest request);
}
