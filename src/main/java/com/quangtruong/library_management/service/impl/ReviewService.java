package com.quangtruong.library_management.service.impl;

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
import com.quangtruong.library_management.service.IReviewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService implements IReviewService {

	IReviewRepository reviewRepository;
	IBookRepository bookRepository;
	IStudentProfileRepository studentProfileRepository;
	IReviewMapper reviewMapper;

	@Override
	public Page<ReviewResponse> getByBook(Long bookId, Pageable pageable) {

		return reviewRepository.findByBookId(bookId, pageable)
				.map(reviewMapper::toResponse);
	}

	@Override
	public ReviewResponse create(Long bookId, ReviewRequest request, UUID userId) {

		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

		StudentProfile student = studentProfileRepository.findById(userId)
				.orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

		// Each student can only review once
		if (reviewRepository.existsByBookIdAndStudentProfileId(bookId, userId)) {
			throw new AppException(ErrorCode.REVIEW_ALREADY_EXISTS);
		}

		Review review = reviewMapper.toEntity(request);
		review.setBook(book);
		review.setStudentProfile(student);
		review.setCreatedAt(LocalDateTime.now());
		review.setUpdatedAt(LocalDateTime.now());

		return reviewMapper.toResponse(reviewRepository.save(review));
	}

	@Override
	public ReviewResponse update(Long reviewId, ReviewRequest request, UUID userId) {
		// Only allow editing own review
		Review review = reviewRepository.findByIdAndStudentProfileId(reviewId, userId)
				.orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_OWNED));

		reviewMapper.update(review, request);
		review.setUpdatedAt(LocalDateTime.now());

		return reviewMapper.toResponse(reviewRepository.save(review));
	}
}
