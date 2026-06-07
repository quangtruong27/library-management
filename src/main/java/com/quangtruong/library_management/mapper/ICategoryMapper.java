package com.quangtruong.library_management.mapper;

import com.quangtruong.library_management.dto.category.CategoryRequest;
import com.quangtruong.library_management.dto.category.CategoryResponse;
import com.quangtruong.library_management.entity.Category;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ICategoryMapper {

	Category toEntity(CategoryRequest request);

	CategoryResponse toResponse(Category category);

	// When updating object, if any request field is null, DO NOT overwrite the current entity.
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void update(@MappingTarget Category category, CategoryRequest request);
}