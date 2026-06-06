package com.quangtruong.library_management.controller;

import com.quangtruong.library_management.dto.ApiResponse;
import com.quangtruong.library_management.dto.PageResponse;
import com.quangtruong.library_management.dto.staff.StaffProfileRequest;
import com.quangtruong.library_management.dto.staff.StaffProfileResponse;
import com.quangtruong.library_management.entity.StaffProfile;
import com.quangtruong.library_management.mapper.IStaffProfileMapper;
import com.quangtruong.library_management.service.IStaffProfileService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/staff")
public class StaffProfileController {
	IStaffProfileService staffProfileService;
	IStaffProfileMapper staffProfileMapper;

	// GET LIST
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<StaffProfileResponse>>> getAll(@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){

		Page<StaffProfile> page = staffProfileService.getAll(pageable);

		Page<StaffProfileResponse> responsePage =
				page.map(staffProfileMapper::toResponse);

		return ResponseEntity.ok(ApiResponse.<PageResponse<StaffProfileResponse>>builder()
				.data(new PageResponse<>(responsePage))
				.build());
	}

	// CREATE
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<ApiResponse<StaffProfileResponse>> create(
			@Valid @RequestBody StaffProfileRequest request) {

		StaffProfile savedStaff = staffProfileService.createStaff(request);

		StaffProfileResponse response =
				staffProfileMapper.toResponse(savedStaff);

		return ResponseEntity.status(HttpStatus.CREATED).body(
				ApiResponse.<StaffProfileResponse>builder()
						.data(response)
						.build()
		);
	}

	// UPDATE
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<StaffProfileResponse>> update(
			@PathVariable UUID id,
			@Valid @RequestBody StaffProfileRequest staffProfileRequest
	) {

		// Pass ID and Request down to Service for update
		StaffProfile updatedStaffProfile = staffProfileService.updateStaff(id, staffProfileRequest);

		// Map returned result to Response DTO
		StaffProfileResponse staffProfileResponse = staffProfileMapper.toResponse(updatedStaffProfile);

		return ResponseEntity.status(HttpStatus.OK).body(
				ApiResponse.<StaffProfileResponse>builder()
						.data(staffProfileResponse)
						.build()
		);
	}

	// DELETE
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
		// Call Service directly to delete.
		staffProfileService.deleteStaff(id);

		// No data needs to be returned on deletion, so use <Void>
		return ResponseEntity.status(HttpStatus.OK).body(
				ApiResponse.<Void>builder()
						.message("Deleted staff successfully")
						.build()
		);
	}
}
