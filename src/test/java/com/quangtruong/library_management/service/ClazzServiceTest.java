package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.clazz.ClazzRequest;
import com.quangtruong.library_management.dto.clazz.ClazzResponse;
import com.quangtruong.library_management.entity.Clazz;
import com.quangtruong.library_management.entity.Faculty;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.mapper.IClazzMapper;
import com.quangtruong.library_management.repository.IClazzRepository;
import com.quangtruong.library_management.repository.IFacultyRepository;
import com.quangtruong.library_management.service.impl.ClazzService;
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

class ClazzServiceTest {

	@Mock
	IClazzRepository clazzRepository;

	@Mock
	IFacultyRepository facultyRepository;

	@Mock
	IClazzMapper clazzMapper;

	@InjectMocks
	ClazzService clazzService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void getById_Success() {
		Clazz clazz = Clazz.builder().id(1L).name("Class A").build();
		ClazzResponse response = ClazzResponse.builder().id(1L).name("Class A").build();

		when(clazzRepository.findById(1L)).thenReturn(Optional.of(clazz));
		when(clazzMapper.toResponse(clazz)).thenReturn(response);

		ClazzResponse result = clazzService.getById(1L);

		assertNotNull(result);
		assertEquals("Class A", result.getName());
	}

	@Test
	void getById_NotFound() {
		when(clazzRepository.findById(1L)).thenReturn(Optional.empty());

		AppException exception = assertThrows(AppException.class, () -> clazzService.getById(1L));
		assertEquals(ErrorCode.CLAZZ_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	void getAll_Success() {
		Clazz clazz = Clazz.builder().id(1L).name("Class A").build();
		ClazzResponse response = ClazzResponse.builder().id(1L).name("Class A").build();
		Page<Clazz> page = new PageImpl<>(Collections.singletonList(clazz));

		when(clazzRepository.findAll(any(Pageable.class))).thenReturn(page);
		when(clazzMapper.toResponse(clazz)).thenReturn(response);

		Page<ClazzResponse> result = clazzService.getAll(PageRequest.of(0, 5));

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals("Class A", result.getContent().get(0).getName());
	}

	@Test
	void create_Success() {
		ClazzRequest request = new ClazzRequest("Class B", "2024", 10L);
		Clazz clazz = Clazz.builder().id(2L).name("Class B").build();
		Faculty faculty = Faculty.builder().id(10L).name("IT").build();
		ClazzResponse response = ClazzResponse.builder().id(2L).name("Class B").build();

		when(clazzRepository.existsByName("Class B")).thenReturn(false);
		when(clazzMapper.toEntity(request)).thenReturn(clazz);
		when(facultyRepository.findById(10L)).thenReturn(Optional.of(faculty));
		when(clazzRepository.save(clazz)).thenReturn(clazz);
		when(clazzMapper.toResponse(clazz)).thenReturn(response);

		ClazzResponse result = clazzService.create(request);

		assertNotNull(result);
		assertEquals("Class B", result.getName());
		assertEquals(faculty, clazz.getFaculty());
	}

	@Test
	void create_AlreadyExists() {
		ClazzRequest request = new ClazzRequest("Class B", null, null);
		when(clazzRepository.existsByName("Class B")).thenReturn(true);

		AppException exception = assertThrows(AppException.class, () -> clazzService.create(request));
		assertEquals(ErrorCode.CLAZZ_NAME_EXISTS, exception.getErrorCode());
	}

	@Test
	void update_Success() {
		ClazzRequest request = new ClazzRequest("Class New", "2024", 11L);
		Clazz clazz = Clazz.builder().id(1L).name("Class Old").build();
		Faculty faculty = Faculty.builder().id(11L).name("Business").build();
		ClazzResponse response = ClazzResponse.builder().id(1L).name("Class New").build();

		when(clazzRepository.findById(1L)).thenReturn(Optional.of(clazz));
		when(clazzRepository.existsByName("Class New")).thenReturn(false);
		when(facultyRepository.findById(11L)).thenReturn(Optional.of(faculty));
		when(clazzRepository.save(clazz)).thenReturn(clazz);
		when(clazzMapper.toResponse(clazz)).thenReturn(response);

		ClazzResponse result = clazzService.update(1L, request);

		assertNotNull(result);
		assertEquals("Class New", result.getName());
		assertEquals(faculty, clazz.getFaculty());
		verify(clazzMapper).update(clazz, request);
	}

	@Test
	void delete_Success() {
		clazzService.delete(1L);
		verify(clazzRepository).deleteById(1L);
	}
}
