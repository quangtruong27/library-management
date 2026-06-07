package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.faculty.FacultyRequest;
import com.quangtruong.library_management.dto.faculty.FacultyResponse;
import com.quangtruong.library_management.entity.Faculty;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.mapper.IFacultyMapper;
import com.quangtruong.library_management.repository.IFacultyRepository;
import com.quangtruong.library_management.service.impl.FacultyService;
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

class FacultyServiceTest {

	@Mock
	IFacultyRepository facultyRepository;

	@Mock
	IFacultyMapper facultyMapper;

	@InjectMocks
	FacultyService facultyService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void getById_Success() {
		Faculty faculty = Faculty.builder().id(1L).name("IT").build();
		FacultyResponse response = FacultyResponse.builder().id(1L).name("IT").build();

		when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
		when(facultyMapper.toResponse(faculty)).thenReturn(response);

		FacultyResponse result = facultyService.getById(1L);

		assertNotNull(result);
		assertEquals("IT", result.getName());
	}

	@Test
	void getById_NotFound() {
		when(facultyRepository.findById(1L)).thenReturn(Optional.empty());

		AppException exception = assertThrows(AppException.class, () -> facultyService.getById(1L));
		assertEquals(ErrorCode.FACULTY_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	void getAll_Success() {
		Faculty faculty = Faculty.builder().id(1L).name("IT").build();
		FacultyResponse response = FacultyResponse.builder().id(1L).name("IT").build();
		Page<Faculty> page = new PageImpl<>(Collections.singletonList(faculty));

		when(facultyRepository.findAll(any(Pageable.class))).thenReturn(page);
		when(facultyMapper.toResponse(faculty)).thenReturn(response);

		Page<FacultyResponse> result = facultyService.getAll(PageRequest.of(0, 5));

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals("IT", result.getContent().get(0).getName());
	}

	@Test
	void create_Success() {
		FacultyRequest request = new FacultyRequest("Business");
		Faculty faculty = Faculty.builder().id(2L).name("Business").build();
		FacultyResponse response = FacultyResponse.builder().id(2L).name("Business").build();

		when(facultyRepository.existsByName("Business")).thenReturn(false);
		when(facultyMapper.toEntity(request)).thenReturn(faculty);
		when(facultyRepository.save(faculty)).thenReturn(faculty);
		when(facultyMapper.toResponse(faculty)).thenReturn(response);

		FacultyResponse result = facultyService.create(request);

		assertNotNull(result);
		assertEquals("Business", result.getName());
	}

	@Test
	void create_AlreadyExists() {
		FacultyRequest request = new FacultyRequest("Business");
		when(facultyRepository.existsByName("Business")).thenReturn(true);

		AppException exception = assertThrows(AppException.class, () -> facultyService.create(request));
		assertEquals(ErrorCode.FACULTY_NAME_EXISTS, exception.getErrorCode());
	}

	@Test
	void update_Success() {
		FacultyRequest request = new FacultyRequest("IT New");
		Faculty faculty = Faculty.builder().id(1L).name("IT Old").build();
		FacultyResponse response = FacultyResponse.builder().id(1L).name("IT New").build();

		when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
		when(facultyRepository.existsByName("IT New")).thenReturn(false);
		when(facultyRepository.save(faculty)).thenReturn(faculty);
		when(facultyMapper.toResponse(faculty)).thenReturn(response);

		FacultyResponse result = facultyService.update(1L, request);

		assertNotNull(result);
		assertEquals("IT New", result.getName());
		verify(facultyMapper).update(faculty, request);
	}

	@Test
	void delete_Success() {
		facultyService.delete(1L);
		verify(facultyRepository).deleteById(1L);
	}
}
