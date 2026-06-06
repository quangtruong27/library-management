package com.quangtruong.library_management.controller;

import com.quangtruong.library_management.dto.ApiResponse;
import com.quangtruong.library_management.dto.PageResponse;
import com.quangtruong.library_management.dto.student.StudentProfileRequest;
import com.quangtruong.library_management.dto.student.StudentProfileResponse;
import com.quangtruong.library_management.service.IStudentProfileService;
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

import java.util.UUID;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/students")
public class StudentProfileController {

	IStudentProfileService studentProfileService;

	@GetMapping
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<PageResponse<StudentProfileResponse>>> getAll(
			@PageableDefault(size = 5, sort = "user.name", direction = Sort.Direction.ASC) Pageable pageable) {

		return ResponseEntity.ok(ApiResponse.<PageResponse<StudentProfileResponse>>builder()
				.data(new PageResponse<>(studentProfileService.getAll(pageable)))
				.build());
	}

	@GetMapping("/{id}")
	// ADMIN has access OR STAFF has access OR the owner user has access to their own data
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF') or (isAuthenticated() " +
			"and principal.claims['sub'] == #id.toString())")
	public ResponseEntity<ApiResponse<StudentProfileResponse>> getById(@PathVariable UUID id) {

		return ResponseEntity.ok(ApiResponse.<StudentProfileResponse>builder()
				.data(studentProfileService.getById(id))
				.build());
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<StudentProfileResponse>> create(@Valid @RequestBody StudentProfileRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.<StudentProfileResponse>builder()
				.data(studentProfileService.create(request))
						.build());
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<StudentProfileResponse>> update(
			@PathVariable UUID id, @Valid @RequestBody StudentProfileRequest request) {

		return ResponseEntity.ok(ApiResponse.<StudentProfileResponse>builder()
				.data(studentProfileService.update(id, request))
				.build());
	}

	// Lock / Unlock account
	@PatchMapping("/{id}/status")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<String>> updateStatus(
			@PathVariable UUID id, @RequestParam Long statusId) {

		studentProfileService.updateStatus(id, statusId);

		return ResponseEntity.ok(ApiResponse.<String>builder()
				.data("Cập nhật trạng thái thành công")
				.build());
	}

	// Reset password
	@PostMapping("/{id}/reset-password")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<String>> resetPassword(@PathVariable UUID id) {

		studentProfileService.resetPassword(id);

		return ResponseEntity.ok(ApiResponse.<String>builder()
				.data("Đã reset mật khẩu về mặc định (123456)")
				.build());
	}

	@GetMapping("/{id}/borrow-history")
	public ResponseEntity<ApiResponse<String>> getBorrowHistory(@PathVariable UUID id) {
		return ResponseEntity.ok(ApiResponse.<String>builder()
				.data("Đang phát triển")
				.build());
	}

	@GetMapping("/{id}/violations")
	public ResponseEntity<ApiResponse<String>> getViolations(@PathVariable UUID id) {
		return ResponseEntity.ok(ApiResponse.<String>builder()
				.data("Đang phát triển")
				.build());
	}
}
