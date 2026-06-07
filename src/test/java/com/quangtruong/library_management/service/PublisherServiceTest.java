package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.publisher.PublisherRequest;
import com.quangtruong.library_management.dto.publisher.PublisherResponse;
import com.quangtruong.library_management.entity.Publisher;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.mapper.IPublisherMapper;
import com.quangtruong.library_management.repository.IPublisherRepository;
import com.quangtruong.library_management.service.impl.PublisherService;
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

class PublisherServiceTest {

	@Mock
	IPublisherRepository publisherRepository;

	@Mock
	IPublisherMapper publisherMapper;

	@InjectMocks
	PublisherService publisherService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void getById_Success() {
		Publisher publisher = Publisher.builder().id(1L).name("O'Reilly").build();
		PublisherResponse response = PublisherResponse.builder().id(1L).name("O'Reilly").build();

		when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));
		when(publisherMapper.toResponse(publisher)).thenReturn(response);

		PublisherResponse result = publisherService.getById(1L);

		assertNotNull(result);
		assertEquals("O'Reilly", result.getName());
	}

	@Test
	void getById_NotFound() {
		when(publisherRepository.findById(1L)).thenReturn(Optional.empty());

		AppException exception = assertThrows(AppException.class, () -> publisherService.getById(1L));
		assertEquals(ErrorCode.PUBLISHER_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	void getAll_Success() {
		Publisher publisher = Publisher.builder().id(1L).name("O'Reilly").build();
		PublisherResponse response = PublisherResponse.builder().id(1L).name("O'Reilly").build();
		Page<Publisher> page = new PageImpl<>(Collections.singletonList(publisher));

		when(publisherRepository.findAll(any(Pageable.class))).thenReturn(page);
		when(publisherMapper.toResponse(publisher)).thenReturn(response);

		Page<PublisherResponse> result = publisherService.getAll(PageRequest.of(0, 5));

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals("O'Reilly", result.getContent().get(0).getName());
	}

	@Test
	void create_Success() {
		PublisherRequest request = new PublisherRequest("Pearson", null, null);
		Publisher publisher = Publisher.builder().id(2L).name("Pearson").build();
		PublisherResponse response = PublisherResponse.builder().id(2L).name("Pearson").build();

		when(publisherMapper.toEntity(request)).thenReturn(publisher);
		when(publisherRepository.save(publisher)).thenReturn(publisher);
		when(publisherMapper.toResponse(publisher)).thenReturn(response);

		PublisherResponse result = publisherService.create(request);

		assertNotNull(result);
		assertEquals("Pearson", result.getName());
	}

	@Test
	void update_Success() {
		PublisherRequest request = new PublisherRequest("New Name", null, null);
		Publisher publisher = Publisher.builder().id(1L).name("Old Name").build();
		PublisherResponse response = PublisherResponse.builder().id(1L).name("New Name").build();

		when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));
		when(publisherRepository.save(publisher)).thenReturn(publisher);
		when(publisherMapper.toResponse(publisher)).thenReturn(response);

		PublisherResponse result = publisherService.update(1L, request);

		assertNotNull(result);
		assertEquals("New Name", result.getName());
		verify(publisherMapper).update(publisher, request);
	}

	@Test
	void update_NotFound() {
		PublisherRequest request = new PublisherRequest("New Name", null, null);
		when(publisherRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(AppException.class, () -> publisherService.update(1L, request));
	}

	@Test
	void delete_Success() {
		publisherService.delete(1L);
		verify(publisherRepository).deleteById(1L);
	}
}
