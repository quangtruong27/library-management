package com.quangtruong.library_management.mapper;

import com.quangtruong.library_management.dto.publisher.PublisherRequest;
import com.quangtruong.library_management.dto.publisher.PublisherResponse;
import com.quangtruong.library_management.entity.Publisher;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
		componentModel = "spring",
		unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IPublisherMapper {

	Publisher toEntity(PublisherRequest request);

	PublisherResponse toResponse(Publisher entity);

	void update(@MappingTarget Publisher entity, PublisherRequest request);
}
