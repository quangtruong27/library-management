package com.quangtruong.library_management.controller;

import com.quangtruong.library_management.dto.ApiResponse;
import com.quangtruong.library_management.dto.PageResponse;
import com.quangtruong.library_management.dto.author.AuthorRequest;
import com.quangtruong.library_management.dto.author.AuthorResponse;
import com.quangtruong.library_management.mapper.IAuthorMapper;
import com.quangtruong.library_management.service.IAuthorService;
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
@RequestMapping("/api/authors")
public class AuthorController {
	IAuthorService authorService;
	IAuthorMapper authorMapper;

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<AuthorResponse>> getById(
			@PathVariable Long id
	) {

		return ResponseEntity.ok(
				ApiResponse.<AuthorResponse>builder()
						.data(authorService.getById(id))
						.build()
		);
	}

	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<AuthorResponse>>> getAll(
			@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
	) {

		Page<AuthorResponse> responsePage =
				authorService.getAll(pageable);

		return ResponseEntity.ok(
				ApiResponse.<PageResponse<AuthorResponse>>builder()
						.data(new PageResponse<>(responsePage))
						.build()
		);
	}

	@PreAuthorize("hasAuthority('AUTHOR_CREATE')")
	@PostMapping
	public ResponseEntity<ApiResponse<AuthorResponse>> create(
			@Valid @RequestBody AuthorRequest request
	) {

		AuthorResponse response =
				authorService.createAuthor(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(
				ApiResponse.<AuthorResponse>builder()
						.data(response)
						.build()
		);
	}

	@PreAuthorize("hasAuthority('AUTHOR_UPDATE')")
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<AuthorResponse>> update(
			@PathVariable Long id,
			@Valid @RequestBody AuthorRequest request
	) {

		AuthorResponse response =
				authorService.updateAuthor(id, request);

		return ResponseEntity.ok(
				ApiResponse.<AuthorResponse>builder()
						.data(response)
						.build()
		);
	}

	@PreAuthorize("hasAuthority('AUTHOR_DELETE')")
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<String>> delete(
			@PathVariable Long id
	) {

		authorService.deleteAuthor(id);

		return ResponseEntity.ok(
				ApiResponse.<String>builder()
						.data("Delete author successfully")
						.build()
		);
	}
}
