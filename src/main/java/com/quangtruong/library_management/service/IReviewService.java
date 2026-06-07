package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.review.ReviewRequest;
import com.quangtruong.library_management.dto.review.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IReviewService {
	Page<ReviewResponse> getByBook(Long bookId, Pageable pageable);
	ReviewResponse create(Long bookId, ReviewRequest request, UUID userId);
	ReviewResponse update(Long reviewId, ReviewRequest request, UUID userId);
}
