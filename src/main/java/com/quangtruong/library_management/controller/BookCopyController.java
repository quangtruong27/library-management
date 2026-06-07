package com.quangtruong.library_management.controller;

import com.quangtruong.library_management.dto.ApiResponse;
import com.quangtruong.library_management.dto.PageResponse;
import com.quangtruong.library_management.dto.bookcopy.BookCopyResponse;
import com.quangtruong.library_management.dto.bookcopy.BookCopyDetailResponse;
import com.quangtruong.library_management.dto.bookcopy.BookCopyStatusUpdateRequest;
import com.quangtruong.library_management.service.IBookCopyService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/book-copies")
public class BookCopyController {

	IBookCopyService bookCopyService;

	@GetMapping
	@PreAuthorize("hasAuthority('BOOK_READ')")
	public ResponseEntity<ApiResponse<PageResponse<BookCopyResponse>>> getBookCopies(
			@RequestParam(required = false) Long bookId,
			@RequestParam(required = false) String status,
			@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

		return ResponseEntity.ok(ApiResponse.<PageResponse<BookCopyResponse>>builder()
				.data(new PageResponse<>(bookCopyService.getBookCopies(bookId, status, pageable)))
				.build());
	}

	@GetMapping("/{copyId}")
	@PreAuthorize("hasAuthority('BOOK_READ')")
	public ResponseEntity<ApiResponse<BookCopyDetailResponse>> getBookCopyDetails(@PathVariable Long copyId) {

		return ResponseEntity.ok(
				ApiResponse.<BookCopyDetailResponse>builder()
						.data(bookCopyService.getBookCopyDetails(copyId))
						.build()
		);
	}

	@PatchMapping("/{copyId}/circulation-status")
	@PreAuthorize("hasAuthority('BOOK_UPDATE')")
	public ResponseEntity<ApiResponse<String>> updateCirculationStatus(
			@PathVariable Long copyId,
			@RequestBody BookCopyStatusUpdateRequest request) {

		bookCopyService.updateCirculationStatus(copyId, request);

		return ResponseEntity.ok(ApiResponse.<String>builder()
				.build());
	}

	@DeleteMapping("/{copyId}")
	@PreAuthorize("hasAuthority('BOOK_DELETE')")
	public ResponseEntity<ApiResponse<String>> deleteBookCopy(@PathVariable Long copyId) {

		bookCopyService.deleteBookCopy(copyId);
		return ResponseEntity.ok(ApiResponse.<String>builder()
				.build());
	}
}
