package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.role.RoleRequest;
import com.quangtruong.library_management.dto.role.RoleResponse;
import com.quangtruong.library_management.entity.Permission;
import com.quangtruong.library_management.entity.Role;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.mapper.IRoleMapper;
import com.quangtruong.library_management.repository.IPermissionRepository;
import com.quangtruong.library_management.repository.IRoleRepository;
import com.quangtruong.library_management.service.impl.RoleService;
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
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RoleServiceTest {

	@Mock
	IRoleRepository roleRepository;

	@Mock
	IPermissionRepository permissionRepository;

	@Mock
	IRoleMapper roleMapper;

	@InjectMocks
	RoleService roleService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void getById_Success() {
		Role role = Role.builder().id(1L).name("LIBRARIAN").build();
		RoleResponse response = RoleResponse.builder().id(1L).name("LIBRARIAN").build();

		when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
		when(roleMapper.toResponse(role)).thenReturn(response);

		RoleResponse result = roleService.getById(1L);

		assertNotNull(result);
		assertEquals("LIBRARIAN", result.getName());
	}

	@Test
	void getById_NotFound() {
		when(roleRepository.findById(1L)).thenReturn(Optional.empty());

		AppException exception = assertThrows(AppException.class, () -> roleService.getById(1L));
		assertEquals(ErrorCode.ROLE_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	void getAll_Success() {
		Role role = Role.builder().id(1L).name("LIBRARIAN").build();
		RoleResponse response = RoleResponse.builder().id(1L).name("LIBRARIAN").build();
		Page<Role> page = new PageImpl<>(Collections.singletonList(role));

		when(roleRepository.findAll(any(Pageable.class))).thenReturn(page);
		when(roleMapper.toResponse(role)).thenReturn(response);

		Page<RoleResponse> result = roleService.getAll(PageRequest.of(0, 5));

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals("LIBRARIAN", result.getContent().get(0).getName());
	}

	@Test
	void create_Success() {
		RoleRequest request = new RoleRequest("ADMIN", null, new HashSet<>(Collections.singletonList(1L)));
		Role role = Role.builder().id(2L).name("ADMIN").permissions(new HashSet<>()).build();
		Permission permission = Permission.builder().id(1L).name("USER_READ").build();
		RoleResponse response = RoleResponse.builder().id(2L).name("ADMIN").build();

		when(roleMapper.toEntity(request)).thenReturn(role);
		when(permissionRepository.findAllById(request.getPermissions())).thenReturn(Collections.singletonList(permission));
		when(roleRepository.save(role)).thenReturn(role);
		when(roleMapper.toResponse(role)).thenReturn(response);

		RoleResponse result = roleService.create(request);

		assertNotNull(result);
		assertEquals("ADMIN", result.getName());
		assertEquals(1, role.getPermissions().size());
	}

	@Test
	void update_Success() {
		RoleRequest request = new RoleRequest("NEW_ROLE", null, new HashSet<>(Collections.singletonList(2L)));
		Role role = Role.builder().id(1L).name("OLD_ROLE").permissions(new HashSet<>()).build();
		Permission permission = Permission.builder().id(2L).name("USER_WRITE").build();
		RoleResponse response = RoleResponse.builder().id(1L).name("NEW_ROLE").build();

		when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
		when(permissionRepository.findAllById(request.getPermissions())).thenReturn(Collections.singletonList(permission));
		when(roleRepository.save(role)).thenReturn(role);
		when(roleMapper.toResponse(role)).thenReturn(response);

		RoleResponse result = roleService.update(1L, request);

		assertNotNull(result);
		assertEquals("NEW_ROLE", result.getName());
		assertEquals(1, role.getPermissions().size());
		verify(roleMapper).update(role, request);
	}

	@Test
	void update_NotFound() {
		RoleRequest request = new RoleRequest("NEW_ROLE", null, null);
		when(roleRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(AppException.class, () -> roleService.update(1L, request));
	}

	@Test
	void delete_Success() {
		roleService.delete(1L);
		verify(roleRepository).deleteById(1L);
	}
}
