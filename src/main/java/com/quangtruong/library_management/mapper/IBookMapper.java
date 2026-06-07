package com.quangtruong.library_management.mapper;

import com.quangtruong.library_management.dto.book.BookRequest;
import com.quangtruong.library_management.dto.book.BookResponse;
import com.quangtruong.library_management.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
		componentModel = "spring",
		unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE // Ignore warning when there are unmapped fields
)
public interface IBookMapper {

	/*
	Create
	Request -> Entity
	 */
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "publisher", ignore = true)
	@Mapping(target = "authors", ignore = true)
	Book toEntity(BookRequest request);

	/*
	Entity -> Response
	 */
	@Mapping(source = "category.id", target = "categoryId")
	@Mapping(source = "category.name", target = "categoryName")
	@Mapping(source = "publisher.id", target = "publisherId")
	@Mapping(source = "publisher.name", target = "publisherName")
	@Mapping(target = "authorId", ignore = true) // Will be set automatically in Service
	@Mapping(target = "authorName", ignore = true) // Will be set automatically in Service
	@Mapping(target = "totalCopies", ignore = true) // Will be calculated automatically in Service
	@Mapping(target = "availableCopies", ignore = true) // Will be calculated automatically in Service
	@Mapping(target = "copies", ignore = true) // Will be set automatically in Service
	BookResponse toResponse(Book entity);

	/*
	Update
	 */
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "publisher", ignore = true)
	@Mapping(target = "authors", ignore = true)
	void update(
			@MappingTarget Book entity,
			BookRequest request
	);
}
