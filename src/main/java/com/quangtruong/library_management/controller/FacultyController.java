package com.quangtruong.library_management.controller;

import com.quangtruong.library_management.dto.ApiResponse;
import com.quangtruong.library_management.dto.PageResponse;
import com.quangtruong.library_management.dto.faculty.FacultyRequest;
import com.quangtruong.library_management.dto.faculty.FacultyResponse;
import com.quangtruong.library_management.service.IFacultyService;
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
@RequestMapping("/api/faculties")
public class FacultyController {

	IFacultyService facultyService;

	@GetMapping
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<PageResponse<FacultyResponse>>> getAll(
			@PageableDefault(size = 5, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

		return ResponseEntity.ok(ApiResponse.<PageResponse<FacultyResponse>>builder()
				.data(new PageResponse<>(facultyService.getAll(pageable)))
				.build());
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<FacultyResponse>> getById(@PathVariable Long id) {

		return ResponseEntity.ok(ApiResponse.<FacultyResponse>builder()
				.data(facultyService.getById(id))
				.build());
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<FacultyResponse>> create(@Valid @RequestBody FacultyRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.<FacultyResponse>builder()
						.data(facultyService.create(request))
						.build());
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<FacultyResponse>> update(
			@PathVariable Long id, @Valid @RequestBody FacultyRequest request) {

		return ResponseEntity.ok(ApiResponse.<FacultyResponse>builder()
				.data(facultyService.update(id, request))
				.build());
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {

		facultyService.delete(id);

		return ResponseEntity.ok(ApiResponse.<String>builder()
				.data("Deleted successfully")
				.build());
	}
}
