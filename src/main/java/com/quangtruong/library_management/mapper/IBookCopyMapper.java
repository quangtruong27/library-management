package com.quangtruong.library_management.mapper;

import com.quangtruong.library_management.dto.bookcopy.BookCopyResponse;
import com.quangtruong.library_management.entity.BookCopy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
		componentModel = "spring",
		unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE // Ignore warning when there are unmapped fields
)
public interface IBookCopyMapper {

	/*
	Entity -> Response
	 */
	@Mapping(source = "bookLocation.name", target = "locationName")
	BookCopyResponse toResponse(BookCopy entity);
}
