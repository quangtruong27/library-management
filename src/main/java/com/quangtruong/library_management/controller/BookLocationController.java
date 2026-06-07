package com.quangtruong.library_management.controller;

import com.quangtruong.library_management.dto.ApiResponse;
import com.quangtruong.library_management.dto.PageResponse;
import com.quangtruong.library_management.dto.booklocation.BookLocationRequest;
import com.quangtruong.library_management.dto.booklocation.BookLocationResponse;
import com.quangtruong.library_management.service.IBookLocationService;
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

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/book-locations")
public class BookLocationController {

	IBookLocationService bookLocationService;

	@PreAuthorize("hasAuthority('BOOK_LOCATION_READ')")
	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<BookLocationResponse>>> getAll(
			@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

		Page<BookLocationResponse> page = bookLocationService.getAll(pageable);

		return ResponseEntity.ok(ApiResponse.<PageResponse<BookLocationResponse>>builder()
				.data(new PageResponse<>(page))
				.build());
	}

	@PreAuthorize("hasAuthority('BOOK_LOCATION_READ')")
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<BookLocationResponse>> getById(@PathVariable Long id) {

		return ResponseEntity.ok(
				ApiResponse.<BookLocationResponse>builder()
						.data(bookLocationService.getById(id))
						.build()
		);
	}

	@PreAuthorize("hasAuthority('BOOK_LOCATION_CREATE')")
	@PostMapping
	public ResponseEntity<ApiResponse<BookLocationResponse>> create(
			@Valid @RequestBody BookLocationRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(
				ApiResponse.<BookLocationResponse>builder()
						.data(bookLocationService.create(request))
						.build()
		);
	}

	@PreAuthorize("hasAuthority('BOOK_LOCATION_UPDATE')")
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<BookLocationResponse>> update(
			@PathVariable Long id, @Valid @RequestBody BookLocationRequest request) {

		return ResponseEntity.ok(
				ApiResponse.<BookLocationResponse>builder()
						.data(bookLocationService.update(id, request))
						.build()
		);
	}

	@PreAuthorize("hasAuthority('BOOK_LOCATION_DELETE')")
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
		bookLocationService.delete(id);

		return ResponseEntity.ok(
				ApiResponse.<String>builder()
						.data("Delete author successfully")
						.build()
		);
	}
}
