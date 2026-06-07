package com.quangtruong.library_management.controller;

import com.quangtruong.library_management.dto.ApiResponse;
import com.quangtruong.library_management.dto.PageResponse;
import com.quangtruong.library_management.dto.clazz.ClazzRequest;
import com.quangtruong.library_management.dto.clazz.ClazzResponse;
import com.quangtruong.library_management.service.IClazzService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/classes")
public class ClazzController {

	IClazzService clazzService;

	@GetMapping
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<PageResponse<ClazzResponse>>> getAll(
			@PageableDefault(size = 5, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

		return ResponseEntity.ok(ApiResponse.<PageResponse<ClazzResponse>>builder()
				.data(new PageResponse<>(clazzService.getAll(pageable)))
				.build());
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<ClazzResponse>> getById(@PathVariable Long id) {

		return ResponseEntity.ok(ApiResponse.<ClazzResponse>builder()
				.data(clazzService.getById(id))
				.build());
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<ClazzResponse>> create(@Valid @RequestBody ClazzRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.<ClazzResponse>builder()
						.data(clazzService.create(request))
						.build());
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<ClazzResponse>> update(
			@PathVariable Long id, @Valid @RequestBody ClazzRequest request) {

		return ResponseEntity.ok(ApiResponse.<ClazzResponse>builder()
				.data(clazzService.update(id, request))
				.build());
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {

		clazzService.delete(id);

		return ResponseEntity.ok(ApiResponse.<String>builder()
				.data("Deleted successfully")
				.build());
	}
}
