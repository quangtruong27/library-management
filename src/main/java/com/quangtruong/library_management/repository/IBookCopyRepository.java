package com.quangtruong.library_management.repository;

import com.quangtruong.library_management.entity.BookCopy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IBookCopyRepository extends JpaRepository<BookCopy, Long> {
	List<BookCopy> findByBookId(Long bookId);

	void deleteByBookId(Long bookId);

	@Query("SELECT bc FROM BookCopy bc WHERE " +
			"(:bookId IS NULL OR bc.book.id = :bookId) AND " +
			"(:status IS NULL OR bc.status = :status)")
	Page<BookCopy> findCopies(@Param("bookId") Long bookId, @Param("status") String status, Pageable pageable);
}
