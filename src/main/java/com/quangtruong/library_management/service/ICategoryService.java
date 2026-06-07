package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.category.CategoryRequest;
import com.quangtruong.library_management.dto.category.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ICategoryService {
	CategoryResponse create(CategoryRequest categoryRequest);
	CategoryResponse update(Long id, CategoryRequest request);
	CategoryResponse getById(Long id);
	Page<CategoryResponse> getAll(Pageable pageable);
	void delete(Long id);
}
