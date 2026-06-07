package com.quangtruong.library_management.service.impl;

import com.quangtruong.library_management.dto.student.StudentProfileRequest;
import com.quangtruong.library_management.dto.student.StudentProfileResponse;
import com.quangtruong.library_management.entity.*;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.mapper.IStudentProfileMapper;
import com.quangtruong.library_management.repository.*;
import com.quangtruong.library_management.service.IStudentProfileService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentProfileService implements IStudentProfileService {

	IStudentProfileRepository studentProfileRepository;
	IUserRepository userRepository;
	IUserStatusRepository userStatusRepository;
	IClazzRepository clazzRepository;
	IStudentProfileMapper studentProfileMapper;
	IRoleRepository roleRepository;

	@Override
	public Page<StudentProfileResponse> getAll(Pageable pageable) {

		return studentProfileRepository.findAll(pageable)
				.map(studentProfileMapper::toResponse);
	}

	@Override
	public StudentProfileResponse getById(UUID id) {

		StudentProfile student = studentProfileRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

		return studentProfileMapper.toResponse(student);
	}

	@Override
	@Transactional
	public StudentProfileResponse create(StudentProfileRequest request) {

		if (studentProfileRepository.existsByStudentCode(request.getStudentCode())) {

			throw new AppException(ErrorCode.STUDENT_ALREADY_EXISTS);
		}

		UserStatus status = userStatusRepository.findById(request.getStatusId())
				.orElseThrow(() -> new AppException(ErrorCode.USER_STATUS_NOT_FOUND));

		Long clazzId = request.getClazz() != null ? request.getClazz().getId() : null;
		if (clazzId == null) {
			throw new AppException(ErrorCode.CLAZZ_NOT_FOUND);
		}
		Clazz clazz = clazzRepository.findById(clazzId)
				.orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));

		// 1. Create User first
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

		// Automatically assign STUDENT role to new student account
		Role studentRole = roleRepository.findByName("STUDENT")
				.orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setStatus(status);
		user.setRoles(new HashSet<>(Set.of(studentRole)));
		userRepository.save(user);

		// 2. Create Student Profile linked to that User
		StudentProfile profile = studentProfileMapper.toEntity(request);
		profile.setUser(user);
		profile.setClazz(clazz);
		studentProfileRepository.save(profile);

		return studentProfileMapper.toResponse(profile);
	}

	@Override
	@Transactional
	public StudentProfileResponse update(UUID id, StudentProfileRequest request) {
		StudentProfile profile = studentProfileRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

		Long clazzId = request.getClazz() != null ? request.getClazz().getId() : null;
		if (clazzId == null) {
			throw new AppException(ErrorCode.CLAZZ_NOT_FOUND);
		}
		Clazz clazz = clazzRepository.findById(clazzId)
				.orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));

		// Update Profile
		studentProfileMapper.update(profile, request);
		profile.setClazz(clazz);

		// Update User
		User user = profile.getUser();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		userRepository.save(user);

		studentProfileRepository.save(profile);
		return studentProfileMapper.toResponse(profile);
	}

	@Override
	public void updateStatus(UUID id, Long statusId) {
		StudentProfile profile = studentProfileRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

		UserStatus status = userStatusRepository.findById(statusId)
				.orElseThrow(() -> new AppException(ErrorCode.USER_STATUS_NOT_FOUND));

		User user = profile.getUser();
		user.setStatus(status);
		userRepository.save(user);
	}

	@Override
	public void resetPassword(UUID id) {
		StudentProfile profile = studentProfileRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

		User user = profile.getUser();
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		user.setPassword(passwordEncoder.encode("123456")); // Set default password
		userRepository.save(user);

	}
}
