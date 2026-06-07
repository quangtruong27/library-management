package com.quangtruong.library_management.repository;

import com.quangtruong.library_management.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IBookRepository extends JpaRepository<Book, Long> {

	@Query("SELECT b FROM Book b WHERE " +
			"(:keyword IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
			"(:categoryId IS NULL OR b.category.id = :categoryId) AND " +
			"(:authorId IS NULL OR EXISTS (SELECT a FROM b.authors a WHERE a.id = :authorId))")
	Page<Book> searchBooks(@Param("keyword") String keyword,
						   @Param("categoryId") Long categoryId,
						   @Param("authorId") Long authorId,
						   Pageable pageable);
}
