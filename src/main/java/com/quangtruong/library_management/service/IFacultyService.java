package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.faculty.FacultyRequest;
import com.quangtruong.library_management.dto.faculty.FacultyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IFacultyService {
	Page<FacultyResponse> getAll(Pageable pageable);
	FacultyResponse getById(Long id);
	FacultyResponse create(FacultyRequest request);
	FacultyResponse update(Long id, FacultyRequest request);
	void delete(Long id);
}
