package com.quangtruong.library_management.mapper;

import com.quangtruong.library_management.dto.author.AuthorRequest;
import com.quangtruong.library_management.dto.author.AuthorResponse;
import com.quangtruong.library_management.entity.Author;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface IAuthorMapper {
	// Request -> Entity (Create)
	Author toEntity(AuthorRequest request);

	// Entity -> Response
	AuthorResponse toResponse(Author author);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void update(@MappingTarget Author author, AuthorRequest request);
}
