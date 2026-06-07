package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.category.CategoryRequest;
import com.quangtruong.library_management.dto.category.CategoryResponse;
import com.quangtruong.library_management.entity.Category;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.mapper.ICategoryMapper;
import com.quangtruong.library_management.repository.ICategoryRepository;
import com.quangtruong.library_management.service.impl.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CategoryServiceTest {

	@Mock
	ICategoryRepository categoryRepository;

	@Mock
	ICategoryMapper categoryMapper;

	@InjectMocks
	CategoryService categoryService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void getById_Success() {
		Category category = Category.builder().id(1L).name("Sci-Fi").build();
		CategoryResponse response = CategoryResponse.builder().id(1L).name("Sci-Fi").build();

		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
		when(categoryMapper.toResponse(category)).thenReturn(response);

		CategoryResponse result = categoryService.getById(1L);

		assertNotNull(result);
		assertEquals("Sci-Fi", result.getName());
	}

	@Test
	void getById_NotFound() {
		when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

		AppException exception = assertThrows(AppException.class, () -> categoryService.getById(1L));
		assertEquals(ErrorCode.CATEGORY_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	void getAll_Success() {
		Category category = Category.builder().id(1L).name("Sci-Fi").build();
		CategoryResponse response = CategoryResponse.builder().id(1L).name("Sci-Fi").build();
		Page<Category> categoryPage = new PageImpl<>(Collections.singletonList(category));

		when(categoryRepository.findAll(any(Pageable.class))).thenReturn(categoryPage);
		when(categoryMapper.toResponse(category)).thenReturn(response);

		Page<CategoryResponse> result = categoryService.getAll(PageRequest.of(0, 5));

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals("Sci-Fi", result.getContent().get(0).getName());
	}

	@Test
	void create_Success() {
		CategoryRequest request = CategoryRequest.builder().name("Science").build();
		Category category = Category.builder().id(2L).name("Science").build();
		CategoryResponse response = CategoryResponse.builder().id(2L).name("Science").build();

		when(categoryRepository.existsByName("Science")).thenReturn(false);
		when(categoryMapper.toEntity(request)).thenReturn(category);
		when(categoryRepository.save(category)).thenReturn(category);
		when(categoryMapper.toResponse(category)).thenReturn(response);

		CategoryResponse result = categoryService.create(request);

		assertNotNull(result);
		assertEquals("Science", result.getName());
	}

	@Test
	void create_AlreadyExists() {
		CategoryRequest request = CategoryRequest.builder().name("Science").build();

		when(categoryRepository.existsByName("Science")).thenReturn(true);

		AppException exception = assertThrows(AppException.class, () -> categoryService.create(request));
		assertEquals(ErrorCode.CATEGORY_ALREADY_EXISTS, exception.getErrorCode());
	}

	@Test
	void update_Success() {
		CategoryRequest request = CategoryRequest.builder().name("New Science").build();
		Category category = Category.builder().id(1L).name("Old Science").build();
		CategoryResponse response = CategoryResponse.builder().id(1L).name("New Science").build();

		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
		when(categoryRepository.existsByName("New Science")).thenReturn(false);
		when(categoryRepository.save(category)).thenReturn(category);
		when(categoryMapper.toResponse(category)).thenReturn(response);

		CategoryResponse result = categoryService.update(1L, request);

		assertNotNull(result);
		assertEquals("New Science", result.getName());
		verify(categoryMapper).update(category, request);
	}

	@Test
	void update_AlreadyExists() {
		CategoryRequest request = CategoryRequest.builder().name("Exists Science").build();
		Category category = Category.builder().id(1L).name("Old Science").build();

		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
		when(categoryRepository.existsByName("Exists Science")).thenReturn(true);

		AppException exception = assertThrows(AppException.class, () -> categoryService.update(1L, request));
		assertEquals(ErrorCode.CATEGORY_ALREADY_EXISTS, exception.getErrorCode());
	}

	@Test
	void delete_Success() {
		Category category = Category.builder().id(1L).name("Sci-Fi").build();
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

		categoryService.delete(1L);

		verify(categoryRepository).delete(category);
	}

	@Test
	void delete_NotFound() {
		when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

		AppException exception = assertThrows(AppException.class, () -> categoryService.delete(1L));
		assertEquals(ErrorCode.CATEGORY_NOT_FOUND, exception.getErrorCode());
	}
}
