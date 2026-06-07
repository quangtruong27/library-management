package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.staff.StaffProfileRequest;
import com.quangtruong.library_management.entity.*;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.mapper.IStaffProfileMapper;
import com.quangtruong.library_management.repository.*;
import com.quangtruong.library_management.service.impl.StaffProfileService;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StaffProfileServiceTest {

	@Mock
	IStaffProfileRepository staffProfileRepository;
	@Mock
	IStaffProfileMapper staffProfileMapper;
	@Mock
	IUserRepository userRepository;
	@Mock
	IUserStatusRepository userStatusRepository;
	@Mock
	IStaffPositionRepository staffPositionRepository;
	@Mock
	IRoleRepository roleRepository;

	@InjectMocks
	StaffProfileService staffProfileService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void getAll_Success() {
		StaffProfile staff = StaffProfile.builder().id(UUID.randomUUID()).build();
		Page<StaffProfile> page = new PageImpl<>(Collections.singletonList(staff));

		when(staffProfileRepository.findAll(any(Pageable.class))).thenReturn(page);

		Page<StaffProfile> result = staffProfileService.getAll(PageRequest.of(0, 5));

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
	}

	@Test
	void createStaff_Success() {
		StaffProfileRequest request = StaffProfileRequest.builder()
				.name("Staff A")
				.email("staffa@gmail.com")
				.password("password123")
				.statusId(1L)
				.position(new StaffProfileRequest.StaffPositionIdRequest(1L))
				.build();

		UserStatus status = UserStatus.builder().id(1L).name("ACTIVE").build();
		StaffPosition position = StaffPosition.builder().id(1L).name("Librarian").build();
		Role role = Role.builder().id(1L).name("STAFF").build();
		User user = User.builder().id(UUID.randomUUID()).email("staffa@gmail.com").build();
		StaffProfile staff = StaffProfile.builder().id(UUID.randomUUID()).employeeCode("EMP001").build();

		when(userStatusRepository.findById(1L)).thenReturn(Optional.of(status));
		when(staffPositionRepository.findById(1L)).thenReturn(Optional.of(position));
		when(roleRepository.findByName("STAFF")).thenReturn(Optional.of(role));
		when(userRepository.save(any(User.class))).thenReturn(user);
		when(staffProfileMapper.toEntity(request)).thenReturn(staff);
		when(staffProfileRepository.save(staff)).thenReturn(staff);

		StaffProfile result = staffProfileService.createStaff(request);

		assertNotNull(result);
		verify(userRepository).save(any(User.class));
		verify(staffProfileRepository).save(staff);
	}

	@Test
	void createStaff_StatusNotFound() {
		StaffProfileRequest request = StaffProfileRequest.builder().statusId(1L).build();
		when(userStatusRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(AppException.class, () -> staffProfileService.createStaff(request));
	}

	@Test
	void createStaff_PositionNotFound() {
		StaffProfileRequest request = StaffProfileRequest.builder()
				.statusId(1L)
				.position(new StaffProfileRequest.StaffPositionIdRequest(1L))
				.build();

		UserStatus status = UserStatus.builder().id(1L).name("ACTIVE").build();
		when(userStatusRepository.findById(1L)).thenReturn(Optional.of(status));
		when(staffPositionRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(AppException.class, () -> staffProfileService.createStaff(request));
	}

	@Test
	void updateStaff_Success() {
		UUID staffId = UUID.randomUUID();
		StaffProfileRequest request = StaffProfileRequest.builder()
				.name("Staff A Updated")
				.email("staffa_updated@gmail.com")
				.phone("0987654321")
				.address("Hanoi")
				.build();

		User user = User.builder().name("Staff A").email("staffa@gmail.com").build();
		StaffProfile existing = StaffProfile.builder()
				.id(staffId)
				.user(user)
				.phone("0123456789")
				.address("HCMC")
				.build();

		when(staffProfileRepository.findById(staffId)).thenReturn(Optional.of(existing));
		when(userRepository.save(user)).thenReturn(user);
		when(staffProfileRepository.save(existing)).thenReturn(existing);

		StaffProfile result = staffProfileService.updateStaff(staffId, request);

		assertNotNull(result);
		assertEquals("0987654321", result.getPhone());
		assertEquals("Hanoi", result.getAddress());
		assertEquals("Staff A Updated", result.getUser().getName());
		assertEquals("staffa_updated@gmail.com", result.getUser().getEmail());
		verify(userRepository).save(user);
		verify(staffProfileRepository).save(existing);
	}

	@Test
	void deleteStaff_Success() {
		UUID staffId = UUID.randomUUID();
		StaffProfile staff = StaffProfile.builder().id(staffId).build();
		when(staffProfileRepository.findById(staffId)).thenReturn(Optional.of(staff));

		staffProfileService.deleteStaff(staffId);

		verify(staffProfileRepository).delete(staff);
	}

	@Test
	void deleteStaff_NotFound() {
		UUID staffId = UUID.randomUUID();
		when(staffProfileRepository.findById(staffId)).thenReturn(Optional.empty());

		assertThrows(AppException.class, () -> staffProfileService.deleteStaff(staffId));
	}
}
