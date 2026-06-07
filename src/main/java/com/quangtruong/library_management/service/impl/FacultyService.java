package com.quangtruong.library_management.service.impl;

import com.quangtruong.library_management.dto.faculty.FacultyRequest;
import com.quangtruong.library_management.dto.faculty.FacultyResponse;
import com.quangtruong.library_management.entity.Faculty;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.mapper.IFacultyMapper;
import com.quangtruong.library_management.repository.IFacultyRepository;
import com.quangtruong.library_management.service.IFacultyService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FacultyService implements IFacultyService {

	IFacultyRepository facultyRepository;
	IFacultyMapper facultyMapper;

	@Override
	public Page<FacultyResponse> getAll(Pageable pageable) {

		return facultyRepository.findAll(pageable)
				.map(facultyMapper::toResponse);
	}

	@Override
	public FacultyResponse getById(Long id) {

		Faculty faculty = facultyRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.FACULTY_NOT_FOUND));
		return facultyMapper.toResponse(faculty);
	}

	@Override
	public FacultyResponse create(FacultyRequest request) {

		if (facultyRepository.existsByName(request.getName())) {
			throw new AppException(ErrorCode.FACULTY_NAME_EXISTS);
		}

		Faculty faculty = facultyMapper.toEntity(request);
		return facultyMapper.toResponse(facultyRepository.save(faculty));
	}

	@Override
	public FacultyResponse update(Long id, FacultyRequest request) {

		Faculty faculty = facultyRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.FACULTY_NOT_FOUND));

		if (!faculty.getName().equals(request.getName()) && facultyRepository.existsByName(request.getName())) {
			throw new AppException(ErrorCode.FACULTY_NAME_EXISTS);
		}

		facultyMapper.update(faculty, request);
		return facultyMapper.toResponse(facultyRepository.save(faculty));
	}

	@Override
	public void delete(Long id) {
		facultyRepository.deleteById(id);
	}
}
