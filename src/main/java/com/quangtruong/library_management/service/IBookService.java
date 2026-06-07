package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.book.BookRequest;
import com.quangtruong.library_management.dto.book.BookResponse;
import com.quangtruong.library_management.dto.book.ShelfAssignRequest;
import com.quangtruong.library_management.dto.book.StockUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBookService {
	Page<BookResponse> searchBooks(String keyword, Long categoryId, Long authorId, Pageable pageable);
	BookResponse createBook(BookRequest request);
	BookResponse getBookDetails(Long bookId);
	BookResponse updateBook(Long bookId, BookRequest request);
	void deleteBook(Long bookId);
	void assignShelf(Long bookId, ShelfAssignRequest request);
	void updateStock(Long bookId, StockUpdateRequest request);
}
