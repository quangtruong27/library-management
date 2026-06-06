package com.quangtruong.library_management.controller;

import com.quangtruong.library_management.dto.ApiResponse;
import com.quangtruong.library_management.dto.PageResponse;
import com.quangtruong.library_management.dto.role.RoleRequest;
import com.quangtruong.library_management.dto.role.RoleResponse;
import com.quangtruong.library_management.service.IRoleService;
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
@RequestMapping("/api/roles")
public class RoleController {

	IRoleService roleService;

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<RoleResponse>>> getAll(
			@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

		return ResponseEntity.ok(ApiResponse.<PageResponse<RoleResponse>>builder()
				.data(new PageResponse<>(roleService.getAll(pageable)))
				.build());
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<RoleResponse>> getById(@PathVariable Long id) {

		return ResponseEntity.ok(ApiResponse.<RoleResponse>builder()
				.data(roleService.getById(id))
				.build());
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<ApiResponse<RoleResponse>> create(@Valid @RequestBody RoleRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.<RoleResponse>builder()
				.data(roleService.create(request))
				.build());
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<RoleResponse>> update(
			@PathVariable Long id, @Valid @RequestBody RoleRequest request) {

		return ResponseEntity.ok(ApiResponse.<RoleResponse>builder()
				.data(roleService.update(id, request))
				.build());
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {

		roleService.delete(id);

		return ResponseEntity.ok(ApiResponse.<String>builder()
				.data("Deleted successfully")
				.build());
	}
}
