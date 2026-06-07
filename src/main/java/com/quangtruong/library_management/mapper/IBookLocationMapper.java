package com.quangtruong.library_management.mapper;

import com.quangtruong.library_management.dto.booklocation.BookLocationRequest;
import com.quangtruong.library_management.dto.booklocation.BookLocationResponse;
import com.quangtruong.library_management.entity.BookLocation;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface IBookLocationMapper {

	BookLocation toEntity(BookLocationRequest request);

	BookLocationResponse toResponse(BookLocation location);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

	void update(@MappingTarget BookLocation location, BookLocationRequest request);

}
