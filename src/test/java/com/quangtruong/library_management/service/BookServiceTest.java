package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.book.*;
import com.quangtruong.library_management.entity.*;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.mapper.IBookCopyMapper;
import com.quangtruong.library_management.mapper.IBookMapper;
import com.quangtruong.library_management.repository.*;
import com.quangtruong.library_management.service.impl.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookServiceTest {

	@Mock
	IBookRepository bookRepository;
	@Mock
	IBookCopyRepository bookCopyRepository;
	@Mock
	ICategoryRepository categoryRepository;
	@Mock
	IPublisherRepository publisherRepository;
	@Mock
	IAuthorRepository authorRepository;
	@Mock
	IBookLocationRepository bookLocationRepository;
	@Mock
	IBookMapper bookMapper;
	@Mock
	IBookCopyMapper bookCopyMapper;

	@InjectMocks
	BookService bookService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	private Book createMockBook() {
		Author author = Author.builder().id(1L).name("Author Name").build();
		Set<Author> authors = new HashSet<>(Collections.singletonList(author));
		return Book.builder()
				.id(1L)
				.name("Java Design Patterns")
				.authors(authors)
				.build();
	}

	private BookResponse createMockBookResponse() {
		return BookResponse.builder()
				.id(1L)
				.name("Java Design Patterns")
				.build();
	}

	@Test
	void searchBooks_Success() {
		Book book = createMockBook();
		BookResponse bookResponse = createMockBookResponse();

		when(bookRepository.searchBooks(any(), any(), any(), any()))
				.thenReturn(new PageImpl<>(Collections.singletonList(book)));
		when(bookMapper.toResponse(book)).thenReturn(bookResponse);
		when(bookCopyRepository.findByBookId(1L)).thenReturn(Collections.emptyList());

		Page<BookResponse> result = bookService.searchBooks("Java", null, null, PageRequest.of(0, 10));

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals("Java Design Patterns", result.getContent().get(0).getName());
	}

	@Test
	void createBook_Success() {
		BookRequest request = BookRequest.builder()
				.name("New Book")
				.price(BigDecimal.valueOf(19.99))
				.publisherYear(2021)
				.category(new BookRequest.CategoryIdRequest(1L))
				.publisher(new BookRequest.PublisherIdRequest(1L))
				.authorId(Collections.singleton(1L))
				.numberOfCopies(2)
				.build();

		Category category = Category.builder().id(1L).name("Tech").build();
		Publisher publisher = Publisher.builder().id(1L).name("O'Reilly").build();
		Author author = Author.builder().id(1L).name("Author Name").build();
		Book book = Book.builder().id(1L).name("New Book").authors(new HashSet<>(Collections.singletonList(author))).build();
		BookResponse bookResponse = BookResponse.builder().id(1L).name("New Book").build();

		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
		when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));
		when(authorRepository.findAllById(request.getAuthorId())).thenReturn(Collections.singletonList(author));
		when(bookMapper.toEntity(request)).thenReturn(book);
		when(bookRepository.save(any(Book.class))).thenReturn(book);
		when(bookMapper.toResponse(book)).thenReturn(bookResponse);

		BookResponse result = bookService.createBook(request);

		assertNotNull(result);
		assertEquals("New Book", result.getName());
		verify(bookCopyRepository, times(2)).save(any(BookCopy.class));
	}

	@Test
	void createBook_CategoryNotFound() {
		BookRequest request = BookRequest.builder()
				.category(new BookRequest.CategoryIdRequest(1L))
				.build();

		when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

		AppException exception = assertThrows(AppException.class, () -> bookService.createBook(request));
		assertEquals(ErrorCode.CATEGORY_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	void getBookDetails_Success() {
		Book book = createMockBook();
		BookResponse response = createMockBookResponse();

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(bookMapper.toResponse(book)).thenReturn(response);
		when(bookCopyRepository.findByBookId(1L)).thenReturn(Collections.emptyList());

		BookResponse result = bookService.getBookDetails(1L);

		assertNotNull(result);
		assertEquals("Java Design Patterns", result.getName());
	}

	@Test
	void getBookDetails_NotFound() {
		when(bookRepository.findById(1L)).thenReturn(Optional.empty());

		AppException exception = assertThrows(AppException.class, () -> bookService.getBookDetails(1L));
		assertEquals(ErrorCode.BOOK_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	void updateBook_Success() {
		BookRequest request = BookRequest.builder().name("Updated Name").build();
		Book book = createMockBook();
		BookResponse response = BookResponse.builder().id(1L).name("Updated Name").build();

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(bookRepository.save(book)).thenReturn(book);
		when(bookMapper.toResponse(book)).thenReturn(response);
		when(bookCopyRepository.findByBookId(1L)).thenReturn(Collections.emptyList());

		BookResponse result = bookService.updateBook(1L, request);

		assertNotNull(result);
		assertEquals("Updated Name", result.getName());
		verify(bookMapper).update(book, request);
	}

	@Test
	void deleteBook_Success() {
		doNothing().when(bookCopyRepository).deleteByBookId(1L);
		doNothing().when(bookRepository).deleteById(1L);

		bookService.deleteBook(1L);

		verify(bookCopyRepository).deleteByBookId(1L);
		verify(bookRepository).deleteById(1L);
	}

	@Test
	void assignShelf_Success() {
		ShelfAssignRequest request = new ShelfAssignRequest(2L);
		BookLocation location = BookLocation.builder().id(2L).name("Shelf A").build();
		BookCopy copy = BookCopy.builder().id(1L).qrCode("QR1").build();

		when(bookLocationRepository.findById(2L)).thenReturn(Optional.of(location));
		when(bookCopyRepository.findByBookId(1L)).thenReturn(Collections.singletonList(copy));

		bookService.assignShelf(1L, request);

		assertEquals(location, copy.getBookLocation());
		verify(bookCopyRepository).save(copy);
	}

	@Test
	void assignShelf_LocationNotFound() {
		ShelfAssignRequest request = new ShelfAssignRequest(2L);

		when(bookLocationRepository.findById(2L)).thenReturn(Optional.empty());

		AppException exception = assertThrows(AppException.class, () -> bookService.assignShelf(1L, request));
		assertEquals(ErrorCode.BOOK_LOCATION_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	void updateStock_Success_Add() {
		StockUpdateRequest request = new StockUpdateRequest("ADD", 3);
		Book book = createMockBook();

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

		bookService.updateStock(1L, request);

		verify(bookCopyRepository, times(3)).save(any(BookCopy.class));
	}
}
