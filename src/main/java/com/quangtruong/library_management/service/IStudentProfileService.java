package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.student.StudentProfileRequest;
import com.quangtruong.library_management.dto.student.StudentProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IStudentProfileService {
	Page<StudentProfileResponse> getAll(Pageable pageable);
	StudentProfileResponse getById(UUID id);
	StudentProfileResponse create(StudentProfileRequest request);
	StudentProfileResponse update(UUID id, StudentProfileRequest request);
	void updateStatus(UUID id, Long statusId);
	void resetPassword(UUID id);
}
