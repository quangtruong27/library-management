package com.quangtruong.library_management.service.impl;

import com.quangtruong.library_management.dto.book.*;
import com.quangtruong.library_management.entity.*;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.mapper.IBookCopyMapper;
import com.quangtruong.library_management.mapper.IBookMapper;
import com.quangtruong.library_management.repository.*;
import com.quangtruong.library_management.service.IBookService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookService implements IBookService {

	IBookRepository bookRepository;
	IBookCopyRepository bookCopyRepository;
	ICategoryRepository categoryRepository;
	IPublisherRepository publisherRepository;
	IAuthorRepository authorRepository;
	IBookLocationRepository bookLocationRepository;

	IBookMapper bookMapper;
	IBookCopyMapper bookCopyMapper;

	@Override
	public Page<BookResponse> searchBooks(String keyword, Long categoryId, Long authorId, Pageable pageable) {
		return bookRepository.searchBooks(keyword, categoryId, authorId, pageable)
				.map(this::enrichBookResponse);
	}

	@Override
	@Transactional
	public BookResponse createBook(BookRequest request) {
		Long categoryId = request.getCategory() != null ? request.getCategory().getId() : null;
		if (categoryId == null) {
			throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
		}
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

		Long publisherId = request.getPublisher() != null ? request.getPublisher().getId() : null;
		if (publisherId == null) {
			throw new AppException(ErrorCode.PUBLISHER_NOT_FOUND);
		}
		Publisher publisher = publisherRepository.findById(publisherId)
				.orElseThrow(() -> new AppException(ErrorCode.PUBLISHER_NOT_FOUND));

		List<Author> authors = authorRepository.findAllById(request.getAuthorId());

		Book book = bookMapper.toEntity(request);
		book.setCategory(category);
		book.setPublisher(publisher);
		book.setAuthors(new java.util.HashSet<>(authors));

		book = bookRepository.save(book);

		// Create copy automatically
		for (int i = 0; i < request.getNumberOfCopies(); i++) {
			BookCopy copy = BookCopy.builder()
					.book(book)
					.qrCode(UUID.randomUUID().toString())
					.status("AVAILABLE")
					.build();
			bookCopyRepository.save(copy);
		}

		return enrichBookResponse(book);
	}

	@Override
	public BookResponse getBookDetails(Long bookId) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
		return enrichBookResponse(book);
	}

	@Override
	@Transactional
	public BookResponse updateBook(Long bookId, BookRequest request) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

		bookMapper.update(book, request);

		book = bookRepository.save(book);
		return enrichBookResponse(book);
	}

	@Override
	@Transactional
	public void deleteBook(Long bookId) {
		bookCopyRepository.deleteByBookId(bookId);
		bookRepository.deleteById(bookId);
	}

	@Override
	@Transactional
	public void assignShelf(Long bookId, ShelfAssignRequest request) {
		BookLocation location = bookLocationRepository.findById(request.getLocationId())
				.orElseThrow(() -> new AppException(ErrorCode.BOOK_LOCATION_NOT_FOUND));

		List<BookCopy> copies = bookCopyRepository.findByBookId(bookId);

		for (BookCopy copy : copies) {
			copy.setBookLocation(location);
			bookCopyRepository.save(copy);
		}
	}

	@Override
	@Transactional
	public void updateStock(Long bookId, StockUpdateRequest request) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

		if ("ADD".equalsIgnoreCase(request.getAction())) {
			for (int i = 0; i < request.getQuantity(); i++) {
				BookCopy copy = BookCopy.builder()
						.book(book)
						.qrCode(UUID.randomUUID().toString())
						.status("AVAILABLE")
						.build();
				bookCopyRepository.save(copy);
			}
		}
	}

	// Handle complex/ignored fields
	private BookResponse enrichBookResponse(Book book) {
		BookResponse response = bookMapper.toResponse(book);

		response.setAuthorId(book.getAuthors()
				.stream()
				.map(Author::getId).
				collect(Collectors.toSet()));

		response.setAuthorName(book.getAuthors()
				.stream()
				.map(Author::getName)
				.collect(Collectors.toSet()));

		List<BookCopy> copies = bookCopyRepository.findByBookId(book.getId());
		response.setTotalCopies(copies.size());
		response.setAvailableCopies((int) copies
				.stream()
				.filter(c -> "AVAILABLE"
						.equals(c.getStatus())).count());
		response.setCopies(copies.stream()
				.map(bookCopyMapper::toResponse)
				.collect(Collectors.toList()));

		return response;
	}
}
