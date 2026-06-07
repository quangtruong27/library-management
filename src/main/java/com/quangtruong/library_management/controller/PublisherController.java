package com.quangtruong.library_management.controller;

import com.quangtruong.library_management.dto.ApiResponse;
import com.quangtruong.library_management.dto.PageResponse;
import com.quangtruong.library_management.dto.publisher.PublisherRequest;
import com.quangtruong.library_management.dto.publisher.PublisherResponse;
import com.quangtruong.library_management.service.IPublisherService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/publishers")
public class PublisherController {

	IPublisherService publisherService;

	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<PublisherResponse>>> getAll(
			@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

		return ResponseEntity.ok(ApiResponse.<PageResponse<PublisherResponse>>builder()
				.data(new PageResponse<>(publisherService.getAll(pageable)))
				.build());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<PublisherResponse>> getById(@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.<PublisherResponse>builder()
				.data(publisherService.getById(id))
				.build());
	}

	@PostMapping
	public ResponseEntity<ApiResponse<PublisherResponse>> create(@Valid @RequestBody PublisherRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.<PublisherResponse>builder()
				.data(publisherService.create(request))
				.build());
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<PublisherResponse>> update(
			@PathVariable Long id, @Valid @RequestBody PublisherRequest request) {

		return ResponseEntity.ok(ApiResponse.<PublisherResponse>builder()
				.data(publisherService.update(id, request))
				.build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {

		publisherService.delete(id);

		return ResponseEntity.ok(ApiResponse.<String>builder()
				.data("Deleted successfully")
				.build());
	}
}
