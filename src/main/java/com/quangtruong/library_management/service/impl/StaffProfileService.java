package com.quangtruong.library_management.service.impl;

import com.quangtruong.library_management.dto.staff.StaffProfileRequest;
import com.quangtruong.library_management.entity.*;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.mapper.IStaffProfileMapper;
import com.quangtruong.library_management.repository.*;
import com.quangtruong.library_management.service.IStaffProfileService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StaffProfileService implements IStaffProfileService {

	IStaffProfileRepository staffProfileRepository;
	IStaffProfileMapper staffProfileMapper;
	IUserRepository userRepository;
	IUserStatusRepository userStatusRepository;
	IStaffPositionRepository staffPositionRepository;
	IRoleRepository roleRepository;

	@Override
	public Page<StaffProfile> getAll(Pageable pageable){
		//staffProfileRepository.findById()
		return staffProfileRepository.findAll(pageable);
	}

	@Override
	@Transactional
	public StaffProfile createStaff(StaffProfileRequest request) {

		UserStatus status = userStatusRepository.findById(request.getStatusId())
				.orElseThrow(() -> new AppException(ErrorCode.USER_STATUS_NOT_FOUND));

		Long positionId = request.getPosition() != null ? request.getPosition().getId() : null;
		if (positionId == null) {
			throw new AppException(ErrorCode.STAFF_POSITION_NOT_FOUND);
		}
		StaffPosition position = staffPositionRepository.findById(positionId)
				.orElseThrow(() -> new AppException(ErrorCode.STAFF_POSITION_NOT_FOUND));

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

		// Automatically assign STAFF role to new staff account
		Role staffRole = roleRepository.findByName("STAFF")
				.orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setStatus(status);
		user.setRoles(new HashSet<>(Set.of(staffRole)));

		userRepository.save(user);

		StaffProfile staff = staffProfileMapper.toEntity(request);
		staff.setUser(user);
		staff.setPosition(position);

		return staffProfileRepository.save(staff);
	}

	@Override
	public StaffProfile updateStaff(UUID id, StaffProfileRequest request) {

		StaffProfile existing = staffProfileRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));

		// update StaffProfile fields
		existing.setPhone(request.getPhone());
		existing.setAddress(request.getAddress());

		// update User if necessary
		User user = existing.getUser();
		user.setName(request.getName());
		user.setEmail(request.getEmail());

		userRepository.save(user);

		return staffProfileRepository.save(existing);
	}

	@Override
	public void deleteStaff(UUID id) {
		StaffProfile existing = staffProfileRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));

		staffProfileRepository.delete(existing);
	}
}
