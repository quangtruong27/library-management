package com.quangtruong.library_management.controller;

import com.quangtruong.library_management.dto.ApiResponse;
import com.quangtruong.library_management.dto.PageResponse;
import com.quangtruong.library_management.dto.book.*;
import com.quangtruong.library_management.dto.book.ShelfAssignRequest;
import com.quangtruong.library_management.dto.book.StockUpdateRequest;
import com.quangtruong.library_management.dto.review.ReviewRequest;
import com.quangtruong.library_management.dto.review.ReviewResponse;
import com.quangtruong.library_management.service.IBookService;
import com.quangtruong.library_management.service.IReviewService;
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
@RequestMapping("/api/books")
public class BookController {

	IBookService bookService;
	IReviewService reviewService;

	// View reviews list of a book
	@GetMapping("/{bookId}/reviews")
	public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getReviews(
			@PathVariable Long bookId,
			@PageableDefault(size = 5, sort = "createdAt",
					direction = Sort.Direction.DESC) Pageable pageable) {
		return ResponseEntity.ok(ApiResponse.<PageResponse<ReviewResponse>>builder()
				.data(new PageResponse<>(reviewService.getByBook(bookId, pageable)))
				.build());
	}

	// Student submits review
	@PostMapping("/{bookId}/reviews")
	@PreAuthorize("hasRole('STUDENT')")
	public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
			@PathVariable Long bookId, @Valid @RequestBody ReviewRequest request) {

		UUID userId = UUID.fromString(
				SecurityContextHolder.getContext().getAuthentication().getName());

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.<ReviewResponse>builder()
						.data(reviewService.create(bookId, request, userId))
						.build());
	}

	// Student updates their review
	@PutMapping("/reviews/{reviewId}")
	@PreAuthorize("hasRole('STUDENT')")
	public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
			@PathVariable Long reviewId, @Valid @RequestBody ReviewRequest request) {

		UUID userId = UUID.fromString(
				SecurityContextHolder.getContext().getAuthentication().getName());

		return ResponseEntity.ok(ApiResponse.<ReviewResponse>builder()
				.data(reviewService.update(reviewId, request, userId))
				.build());
	}

	@GetMapping
	@PreAuthorize("hasAuthority('BOOK_READ')")
	public ResponseEntity<ApiResponse<PageResponse<BookResponse>>> getAll(
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) Long categoryId,
			@RequestParam(required = false) Long authorId,
			@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

		return ResponseEntity.ok(ApiResponse.<PageResponse<BookResponse>>builder()
				.data(new PageResponse<>(bookService.searchBooks(keyword, categoryId, authorId, pageable)))
				.build());
	}

	@PostMapping
	@PreAuthorize("hasAuthority('BOOK_CREATE')")
	public ResponseEntity<ApiResponse<BookResponse>> create(@Valid @RequestBody BookRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(
				ApiResponse.<BookResponse>builder()
						.data(bookService.createBook(request))
						.build()
		);
	}

	@GetMapping("/{bookId}")
	@PreAuthorize("hasAuthority('BOOK_READ')")
	public ResponseEntity<ApiResponse<BookResponse>> getDetails(@PathVariable Long bookId) {

		return ResponseEntity.ok(ApiResponse.<BookResponse>builder()
				.data(bookService.getBookDetails(bookId))
				.build());
	}

	@PutMapping("/{bookId}")
	@PreAuthorize("hasAuthority('BOOK_UPDATE')")
	public ResponseEntity<ApiResponse<BookResponse>> update(
			@PathVariable Long bookId, @Valid @RequestBody BookRequest request) {

		return ResponseEntity.ok(ApiResponse.<BookResponse>builder()
				.data(bookService.updateBook(bookId, request))
				.build());
	}

	@DeleteMapping("/{bookId}")
	@PreAuthorize("hasAuthority('BOOK_DELETE')")
	public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long bookId) {

		bookService.deleteBook(bookId);
		return ResponseEntity.ok(ApiResponse.<String>builder()
				.build());
	}

	@PutMapping("/{bookId}/shelf")
	@PreAuthorize("hasAuthority('BOOK_UPDATE')")
	public ResponseEntity<ApiResponse<String>> assignShelf(
			@PathVariable Long bookId, @RequestBody ShelfAssignRequest request) {

		bookService.assignShelf(bookId, request);
		return ResponseEntity.ok(ApiResponse.<String>builder()
				.build());
	}

	@PatchMapping("/{bookId}/stock")
	@PreAuthorize("hasAuthority('BOOK_UPDATE')")
	public ResponseEntity<ApiResponse<String>> updateStock(
			@PathVariable Long bookId, @RequestBody StockUpdateRequest request) {

		bookService.updateStock(bookId, request);
		return ResponseEntity.ok(ApiResponse.<String>builder()
				.build());
	}
}
