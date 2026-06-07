package com.quangtruong.library_management.mapper;

import com.quangtruong.library_management.dto.fine.FineResponse;
import com.quangtruong.library_management.entity.Fine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
		componentModel = "spring",
		unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IFineMapper {
	@Mapping(source = "borrowDetail.id", target = "borrowDetailId")
	FineResponse toResponse(Fine fine);
}
