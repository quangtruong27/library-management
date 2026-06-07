package com.quangtruong.library_management.repository;

import com.quangtruong.library_management.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IReviewRepository extends JpaRepository<Review, Long> {

	Page<Review> findByBookId(Long bookId, Pageable pageable);

	// Check if student has already reviewed this book
	boolean existsByBookIdAndStudentProfileId(Long bookId, UUID studentId);

	// Find review of a student for a book, to update
	Optional<Review> findByIdAndStudentProfileId(Long id, UUID studentId);
}
