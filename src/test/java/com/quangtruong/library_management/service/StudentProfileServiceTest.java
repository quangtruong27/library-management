package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.student.StudentProfileResponse;
import com.quangtruong.library_management.entity.StudentProfile;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.mapper.IStudentProfileMapper;
import com.quangtruong.library_management.repository.IStudentProfileRepository;
import com.quangtruong.library_management.service.impl.StudentProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class StudentProfileServiceTest {

	@Mock
	IStudentProfileRepository studentProfileRepository;
	@Mock
	IStudentProfileMapper studentProfileMapper;

	@InjectMocks
	StudentProfileService studentProfileService;

	UUID studentId = UUID.randomUUID();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void getById_Success() {
		StudentProfile student = StudentProfile.builder().id(studentId).build();
		when(studentProfileRepository.findById(studentId)).thenReturn(Optional.of(student));

		StudentProfileResponse response = StudentProfileResponse.builder().id(studentId).build();
		when(studentProfileMapper.toResponse(student)).thenReturn(response);

		StudentProfileResponse result = studentProfileService.getById(studentId);

		assertNotNull(result);
		assertEquals(studentId, result.getId());
	}

	@Test
	void getById_NotFound() {
		when(studentProfileRepository.findById(studentId)).thenReturn(Optional.empty());

		assertThrows(AppException.class, () -> studentProfileService.getById(studentId));
	}
}
