package com.quangtruong.library_management.controller;

import com.quangtruong.library_management.dto.ApiResponse;
import com.quangtruong.library_management.dto.PageResponse;
import com.quangtruong.library_management.dto.incident.*;
import com.quangtruong.library_management.service.IIncidentService;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api")
public class IncidentController {

	IIncidentService incidentService;

	private UUID getCurrentUserId() {
		return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
	}

	// 1. List incident tickets
	@GetMapping("/incidents")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<PageResponse<IncidentResponse>>> getAllIncidents(
			@RequestParam(required = false) String status,
			@RequestParam(required = false) String priority,
			@PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

		return ResponseEntity.ok(ApiResponse.<PageResponse<IncidentResponse>>builder()
				.data(new PageResponse<>(incidentService.getAllIncidents(status, priority, pageable)))
				.build());
	}

	// 2. Create incident ticket
	@PostMapping("/incidents")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<IncidentResponse>> createIncident(
			@Valid @RequestBody IncidentCreateRequest request) {

		UUID reporterUserId = getCurrentUserId();
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.<IncidentResponse>builder()
						.data(incidentService.createIncident(request, reporterUserId))
						.build());
	}

	// 3. Update incident (Assignee / status / resolution note)
	@PatchMapping("/incidents/{incidentId}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<IncidentResponse>> updateIncident(
			@PathVariable Long incidentId,
			@Valid @RequestBody IncidentUpdateRequest request) {

		return ResponseEntity.ok(ApiResponse.<IncidentResponse>builder()
				.data(incidentService.updateIncident(incidentId, request))
				.build());
	}

	// 4. View incident detail
	@GetMapping("/incidents/{incidentId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<IncidentResponse>> getIncidentById(@PathVariable Long incidentId) {
		return ResponseEntity.ok(ApiResponse.<IncidentResponse>builder()
				.data(incidentService.getIncidentById(incidentId))
				.build());
	}

	// 5. Close ticket
	@PostMapping("/incidents/{incidentId}/close")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<ApiResponse<IncidentResponse>> closeIncident(@PathVariable Long incidentId) {
		return ResponseEntity.ok(ApiResponse.<IncidentResponse>builder()
				.data(incidentService.closeIncident(incidentId))
				.build());
	}

	// 6. Add comment / exchange on incident
	@PostMapping("/incidents/{incidentId}/comments")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<IncidentCommentResponse>> addComment(
			@PathVariable Long incidentId,
			@Valid @RequestBody IncidentCommentRequest request) {

		UUID userId = getCurrentUserId();
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.<IncidentCommentResponse>builder()
						.data(incidentService.addComment(incidentId, request, userId))
						.build());
	}
}
