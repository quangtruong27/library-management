package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.review.ReviewRequest;
import com.quangtruong.library_management.dto.review.ReviewResponse;
import com.quangtruong.library_management.entity.Book;
import com.quangtruong.library_management.entity.Review;
import com.quangtruong.library_management.entity.StudentProfile;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.mapper.IReviewMapper;
import com.quangtruong.library_management.repository.IBookRepository;
import com.quangtruong.library_management.repository.IReviewRepository;
import com.quangtruong.library_management.repository.IStudentProfileRepository;
import com.quangtruong.library_management.service.impl.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReviewServiceTest {

	@Mock
	IReviewRepository reviewRepository;

	@Mock
	IBookRepository bookRepository;

	@Mock
	IStudentProfileRepository studentProfileRepository;

	@Mock
	IReviewMapper reviewMapper;

	@InjectMocks
	ReviewService reviewService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void getByBook_Success() {
		Review review = Review.builder().id(1L).rating(5).comment("Great!").build();
		ReviewResponse response = ReviewResponse.builder().id(1L).rating(5).comment("Great!").build();
		Page<Review> page = new PageImpl<>(Collections.singletonList(review));

		when(reviewRepository.findByBookId(anyLong(), any(Pageable.class))).thenReturn(page);
		when(reviewMapper.toResponse(review)).thenReturn(response);

		Page<ReviewResponse> result = reviewService.getByBook(1L, PageRequest.of(0, 5));

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals(5, result.getContent().get(0).getRating());
	}

	@Test
	void create_Success() {
		UUID studentId = UUID.randomUUID();
		ReviewRequest request = new ReviewRequest(5, "Excellent");
		Book book = Book.builder().id(1L).build();
		StudentProfile student = StudentProfile.builder().id(studentId).build();
		Review review = Review.builder().id(1L).rating(5).comment("Excellent").build();
		ReviewResponse response = ReviewResponse.builder().id(1L).rating(5).comment("Excellent").build();

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(studentProfileRepository.findById(studentId)).thenReturn(Optional.of(student));
		when(reviewRepository.existsByBookIdAndStudentProfileId(1L, studentId)).thenReturn(false);
		when(reviewMapper.toEntity(request)).thenReturn(review);
		when(reviewRepository.save(review)).thenReturn(review);
		when(reviewMapper.toResponse(review)).thenReturn(response);

		ReviewResponse result = reviewService.create(1L, request, studentId);

		assertNotNull(result);
		assertEquals(5, result.getRating());
		assertEquals("Excellent", result.getComment());
		verify(reviewRepository).save(review);
	}

	@Test
	void create_AlreadyExists() {
		UUID studentId = UUID.randomUUID();
		ReviewRequest request = new ReviewRequest(5, "Excellent");
		Book book = Book.builder().id(1L).build();
		StudentProfile student = StudentProfile.builder().id(studentId).build();

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(studentProfileRepository.findById(studentId)).thenReturn(Optional.of(student));
		when(reviewRepository.existsByBookIdAndStudentProfileId(1L, studentId)).thenReturn(true);

		AppException exception = assertThrows(AppException.class, () -> reviewService.create(1L, request, studentId));
		assertEquals(ErrorCode.REVIEW_ALREADY_EXISTS, exception.getErrorCode());
	}

	@Test
	void update_Success() {
		UUID studentId = UUID.randomUUID();
		ReviewRequest request = new ReviewRequest(4, "Good enough");
		Review review = Review.builder().id(1L).rating(5).comment("Excellent").build();
		ReviewResponse response = ReviewResponse.builder().id(1L).rating(4).comment("Good enough").build();

		when(reviewRepository.findByIdAndStudentProfileId(1L, studentId)).thenReturn(Optional.of(review));
		when(reviewRepository.save(review)).thenReturn(review);
		when(reviewMapper.toResponse(review)).thenReturn(response);

		ReviewResponse result = reviewService.update(1L, request, studentId);

		assertNotNull(result);
		assertEquals(4, result.getRating());
		verify(reviewMapper).update(review, request);
	}

	@Test
	void update_NotOwned() {
		UUID studentId = UUID.randomUUID();
		ReviewRequest request = new ReviewRequest(4, "Good enough");
		when(reviewRepository.findByIdAndStudentProfileId(1L, studentId)).thenReturn(Optional.empty());

		AppException exception = assertThrows(AppException.class, () -> reviewService.update(1L, request, studentId));
		assertEquals(ErrorCode.REVIEW_NOT_OWNED, exception.getErrorCode());
	}
}
