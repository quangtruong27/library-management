package com.quangtruong.library_management.mapper;

import com.quangtruong.library_management.dto.faculty.FacultyRequest;
import com.quangtruong.library_management.dto.faculty.FacultyResponse;
import com.quangtruong.library_management.entity.Faculty;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
		componentModel = "spring",
		unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IFacultyMapper {
	Faculty toEntity(FacultyRequest request);

	FacultyResponse toResponse(Faculty entity);

	void update(@MappingTarget Faculty entity, FacultyRequest request);
}
