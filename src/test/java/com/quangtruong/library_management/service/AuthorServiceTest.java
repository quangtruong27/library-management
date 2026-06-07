package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.author.AuthorRequest;
import com.quangtruong.library_management.dto.author.AuthorResponse;
import com.quangtruong.library_management.entity.Author;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.mapper.IAuthorMapper;
import com.quangtruong.library_management.repository.IAuthorRepository;
import com.quangtruong.library_management.service.impl.AuthorService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthorServiceTest {

	@Mock
	IAuthorRepository authorRepository;

	@Mock
	IAuthorMapper authorMapper;

	@InjectMocks
	AuthorService authorService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void getById_Success() {
		Author author = Author.builder().id(1L).name("Author A").build();
		AuthorResponse response = AuthorResponse.builder().id(1L).name("Author A").build();

		when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
		when(authorMapper.toResponse(author)).thenReturn(response);

		AuthorResponse result = authorService.getById(1L);

		assertNotNull(result);
		assertEquals("Author A", result.getName());
	}

	@Test
	void getById_NotFound() {
		when(authorRepository.findById(1L)).thenReturn(Optional.empty());

		AppException exception = assertThrows(AppException.class, () -> authorService.getById(1L));
		assertEquals(ErrorCode.AUTHOR_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	void getAll_Success() {
		Author author = Author.builder().id(1L).name("Author A").build();
		AuthorResponse response = AuthorResponse.builder().id(1L).name("Author A").build();
		Page<Author> authorPage = new PageImpl<>(Collections.singletonList(author));

		when(authorRepository.findAll(any(Pageable.class))).thenReturn(authorPage);
		when(authorMapper.toResponse(author)).thenReturn(response);

		Page<AuthorResponse> result = authorService.getAll(PageRequest.of(0, 5));

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals("Author A", result.getContent().get(0).getName());
	}

	@Test
	void createAuthor_Success() {
		AuthorRequest request = AuthorRequest.builder().name("Author B").build();
		Author author = Author.builder().id(2L).name("Author B").build();
		AuthorResponse response = AuthorResponse.builder().id(2L).name("Author B").build();

		when(authorRepository.existsByName("Author B")).thenReturn(false);
		when(authorMapper.toEntity(request)).thenReturn(author);
		when(authorRepository.save(author)).thenReturn(author);
		when(authorMapper.toResponse(author)).thenReturn(response);

		AuthorResponse result = authorService.createAuthor(request);

		assertNotNull(result);
		assertEquals("Author B", result.getName());
	}

	@Test
	void createAuthor_AlreadyExists() {
		AuthorRequest request = AuthorRequest.builder().name("Author B").build();

		when(authorRepository.existsByName("Author B")).thenReturn(true);

		AppException exception = assertThrows(AppException.class, () -> authorService.createAuthor(request));
		assertEquals(ErrorCode.AUTHOR_ALREADY_EXISTS, exception.getErrorCode());
	}

	@Test
	void updateAuthor_Success() {
		AuthorRequest request = AuthorRequest.builder().name("New Name").build();
		Author author = Author.builder().id(1L).name("Old Name").build();
		AuthorResponse response = AuthorResponse.builder().id(1L).name("New Name").build();

		when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
		when(authorRepository.existsByName("New Name")).thenReturn(false);
		when(authorRepository.save(author)).thenReturn(author);
		when(authorMapper.toResponse(author)).thenReturn(response);

		AuthorResponse result = authorService.updateAuthor(1L, request);

		assertNotNull(result);
		assertEquals("New Name", result.getName());
		verify(authorMapper).update(author, request);
	}

	@Test
	void updateAuthor_AlreadyExists() {
		AuthorRequest request = AuthorRequest.builder().name("Exists Name").build();
		Author author = Author.builder().id(1L).name("Old Name").build();

		when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
		when(authorRepository.existsByName("Exists Name")).thenReturn(true);

		AppException exception = assertThrows(AppException.class, () -> authorService.updateAuthor(1L, request));
		assertEquals(ErrorCode.AUTHOR_ALREADY_EXISTS, exception.getErrorCode());
	}

	@Test
	void deleteAuthor_Success() {
		Author author = Author.builder().id(1L).name("Author").build();
		when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

		authorService.deleteAuthor(1L);

		verify(authorRepository).delete(author);
	}

	@Test
	void deleteAuthor_NotFound() {
		when(authorRepository.findById(1L)).thenReturn(Optional.empty());

		AppException exception = assertThrows(AppException.class, () -> authorService.deleteAuthor(1L));
		assertEquals(ErrorCode.AUTHOR_NOT_FOUND, exception.getErrorCode());
	}
}
