package com.quangtruong.library_management.service.impl;

import com.quangtruong.library_management.dto.clazz.ClazzRequest;
import com.quangtruong.library_management.dto.clazz.ClazzResponse;
import com.quangtruong.library_management.entity.Clazz;
import com.quangtruong.library_management.entity.Faculty;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.mapper.IClazzMapper;
import com.quangtruong.library_management.repository.IClazzRepository;
import com.quangtruong.library_management.repository.IFacultyRepository;
import com.quangtruong.library_management.service.IClazzService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClazzService implements IClazzService {

	IClazzRepository clazzRepository;
	IFacultyRepository facultyRepository;
	IClazzMapper clazzMapper;

	@Override
	public Page<ClazzResponse> getAll(Pageable pageable) {

		return clazzRepository.findAll(pageable)
				.map(clazzMapper::toResponse);
	}

	@Override
	public ClazzResponse getById(Long id) {

		Clazz clazz = clazzRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));

		return clazzMapper.toResponse(clazz);
	}

	@Override
	public ClazzResponse create(ClazzRequest request) {

		if (clazzRepository.existsByName(request.getName())) {
			throw new AppException(ErrorCode.CLAZZ_NAME_EXISTS);
		}

		Clazz clazz = clazzMapper.toEntity(request);

		if (request.getFacultyId() != null) {
			Faculty faculty = facultyRepository.findById(request.getFacultyId())
					.orElseThrow(() -> new AppException(ErrorCode.FACULTY_NOT_FOUND));
			clazz.setFaculty(faculty);
		}

		return clazzMapper.toResponse(clazzRepository.save(clazz));
	}

	@Override
	public ClazzResponse update(Long id, ClazzRequest request) {

		Clazz clazz = clazzRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));

		// If changing to a new name that already exists, throw error
		if (!clazz.getName().equals(request.getName()) && clazzRepository.existsByName(request.getName())) {
			throw new AppException(ErrorCode.CLAZZ_NAME_EXISTS);
		}

		clazzMapper.update(clazz, request);

		if (request.getFacultyId() != null) {
			Faculty faculty = facultyRepository.findById(request.getFacultyId())
					.orElseThrow(() -> new AppException(ErrorCode.FACULTY_NOT_FOUND));
			clazz.setFaculty(faculty);
		}

		return clazzMapper.toResponse(clazzRepository.save(clazz));
	}

	@Override
	public void delete(Long id) {
		clazzRepository.deleteById(id);
	}
}
