package com.quangtruong.library_management.controller;

import com.quangtruong.library_management.dto.ApiResponse;
import com.quangtruong.library_management.dto.PageResponse;
import com.quangtruong.library_management.dto.category.CategoryRequest;
import com.quangtruong.library_management.dto.category.CategoryResponse;
import com.quangtruong.library_management.service.impl.CategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	CategoryService categoryService;

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable Long id) {
		return ResponseEntity.ok(
				ApiResponse.<CategoryResponse>builder()
						.data(categoryService.getById(id))
						.build()
		);
	}

	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse	<CategoryResponse>>> getAll(
			@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

		Page<CategoryResponse> responsePage =
				categoryService.getAll(pageable);

		return ResponseEntity.ok(
				ApiResponse.<PageResponse<CategoryResponse>>builder()
						.data(new PageResponse<>(responsePage))
						.build()
		);
	}

	@PostMapping
	public ResponseEntity<ApiResponse<CategoryResponse>> create(
			@Valid @RequestBody CategoryRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(
				ApiResponse.<CategoryResponse>builder()
						.data(categoryService.create(request))
						.build()
		);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<CategoryResponse>> update(
			@PathVariable Long id,
			@Valid @RequestBody CategoryRequest request
	) {
		return ResponseEntity.ok(
				ApiResponse.<CategoryResponse>builder()
						.data(categoryService.update(id, request))
						.build()
		);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
		categoryService.delete(id);

		return ResponseEntity.ok(
				ApiResponse.<String>builder()
						.data("Delete category successfully")
						.build()
		);
	}
}