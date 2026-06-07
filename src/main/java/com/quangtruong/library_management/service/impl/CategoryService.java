package com.quangtruong.library_management.service.impl;

import com.quangtruong.library_management.dto.category.CategoryRequest;
import com.quangtruong.library_management.dto.category.CategoryResponse;
import com.quangtruong.library_management.entity.Category;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.mapper.ICategoryMapper;
import com.quangtruong.library_management.repository.ICategoryRepository;
import com.quangtruong.library_management.service.ICategoryService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService implements ICategoryService {

	ICategoryRepository categoryRepository;
	ICategoryMapper categoryMapper;

	@Override
	public CategoryResponse getById(Long id) {

		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

		return categoryMapper.toResponse(category);
	}

	@Override
	public Page<CategoryResponse> getAll(Pageable pageable) {
		return categoryRepository.findAll(pageable)
				.map(categoryMapper::toResponse);
	}

	@Override
	public CategoryResponse create(CategoryRequest request) {

		// check duplicate name
		if (categoryRepository.existsByName(request.getName())) {
			throw new AppException(ErrorCode.CATEGORY_ALREADY_EXISTS);
		}

		Category category = categoryMapper.toEntity(request);

		return categoryMapper.toResponse(categoryRepository.save(category));
	}

	@Override
	public CategoryResponse update(Long id, CategoryRequest request) {

		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

		// if name is changed, check for duplicate
		if (!category.getName().equals(request.getName())
				&& categoryRepository.existsByName(request.getName())) {
			throw new AppException(ErrorCode.CATEGORY_ALREADY_EXISTS);
		}

		categoryMapper.update(category, request);

		return categoryMapper.toResponse(categoryRepository.save(category));
	}

	@Override
	public void delete(Long id) {

		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

		categoryRepository.delete(category);
	}
}