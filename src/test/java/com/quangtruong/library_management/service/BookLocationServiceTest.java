package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.booklocation.BookLocationRequest;
import com.quangtruong.library_management.dto.booklocation.BookLocationResponse;
import com.quangtruong.library_management.entity.BookLocation;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.mapper.IBookLocationMapper;
import com.quangtruong.library_management.repository.IBookLocationRepository;
import com.quangtruong.library_management.service.impl.BookLocationService;
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

class BookLocationServiceTest {

	@Mock
	IBookLocationRepository bookLocationRepository;

	@Mock
	IBookLocationMapper bookLocationMapper;

	@InjectMocks
	BookLocationService bookLocationService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void getById_Success() {
		BookLocation location = BookLocation.builder().id(1L).name("Shelf A").build();
		BookLocationResponse response = BookLocationResponse.builder().id(1L).name("Shelf A").build();

		when(bookLocationRepository.findById(1L)).thenReturn(Optional.of(location));
		when(bookLocationMapper.toResponse(location)).thenReturn(response);

		BookLocationResponse result = bookLocationService.getById(1L);

		assertNotNull(result);
		assertEquals("Shelf A", result.getName());
	}

	@Test
	void getById_NotFound() {
		when(bookLocationRepository.findById(1L)).thenReturn(Optional.empty());

		AppException exception = assertThrows(AppException.class, () -> bookLocationService.getById(1L));
		assertEquals(ErrorCode.BOOK_LOCATION_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	void getAll_Success() {
		BookLocation location = BookLocation.builder().id(1L).name("Shelf A").build();
		BookLocationResponse response = BookLocationResponse.builder().id(1L).name("Shelf A").build();
		Page<BookLocation> page = new PageImpl<>(Collections.singletonList(location));

		when(bookLocationRepository.findAll(any(Pageable.class))).thenReturn(page);
		when(bookLocationMapper.toResponse(location)).thenReturn(response);

		Page<BookLocationResponse> result = bookLocationService.getAll(PageRequest.of(0, 5));

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals("Shelf A", result.getContent().get(0).getName());
	}

	@Test
	void create_Success() {
		BookLocationRequest request = BookLocationRequest.builder().name("Shelf B").build();
		BookLocation location = BookLocation.builder().id(2L).name("Shelf B").build();
		BookLocationResponse response = BookLocationResponse.builder().id(2L).name("Shelf B").build();

		when(bookLocationRepository.existsByName("Shelf B")).thenReturn(false);
		when(bookLocationMapper.toEntity(request)).thenReturn(location);
		when(bookLocationRepository.save(location)).thenReturn(location);
		when(bookLocationMapper.toResponse(location)).thenReturn(response);

		BookLocationResponse result = bookLocationService.create(request);

		assertNotNull(result);
		assertEquals("Shelf B", result.getName());
	}

	@Test
	void create_AlreadyExists() {
		BookLocationRequest request = BookLocationRequest.builder().name("Shelf B").build();

		when(bookLocationRepository.existsByName("Shelf B")).thenReturn(true);

		AppException exception = assertThrows(AppException.class, () -> bookLocationService.create(request));
		assertEquals(ErrorCode.BOOK_LOCATION_ALREADY_EXISTS, exception.getErrorCode());
	}

	@Test
	void update_Success() {
		BookLocationRequest request = BookLocationRequest.builder().name("Shelf New").build();
		BookLocation location = BookLocation.builder().id(1L).name("Shelf Old").build();
		BookLocationResponse response = BookLocationResponse.builder().id(1L).name("Shelf New").build();

		when(bookLocationRepository.findById(1L)).thenReturn(Optional.of(location));
		when(bookLocationRepository.existsByName("Shelf New")).thenReturn(false);
		when(bookLocationRepository.save(location)).thenReturn(location);
		when(bookLocationMapper.toResponse(location)).thenReturn(response);

		BookLocationResponse result = bookLocationService.update(1L, request);

		assertNotNull(result);
		assertEquals("Shelf New", result.getName());
		verify(bookLocationMapper).update(location, request);
	}

	@Test
	void update_AlreadyExists() {
		BookLocationRequest request = BookLocationRequest.builder().name("Shelf Exists").build();
		BookLocation location = BookLocation.builder().id(1L).name("Shelf Old").build();

		when(bookLocationRepository.findById(1L)).thenReturn(Optional.of(location));
		when(bookLocationRepository.existsByName("Shelf Exists")).thenReturn(true);

		AppException exception = assertThrows(AppException.class, () -> bookLocationService.update(1L, request));
		assertEquals(ErrorCode.BOOK_LOCATION_ALREADY_EXISTS, exception.getErrorCode());
	}

	@Test
	void delete_Success() {
		BookLocation location = BookLocation.builder().id(1L).name("Shelf A").build();
		when(bookLocationRepository.findById(1L)).thenReturn(Optional.of(location));

		bookLocationService.delete(1L);

		verify(bookLocationRepository).delete(location);
	}

	@Test
	void delete_NotFound() {
		when(bookLocationRepository.findById(1L)).thenReturn(Optional.empty());

		AppException exception = assertThrows(AppException.class, () -> bookLocationService.delete(1L));
		assertEquals(ErrorCode.BOOK_LOCATION_NOT_FOUND, exception.getErrorCode());
	}
}
