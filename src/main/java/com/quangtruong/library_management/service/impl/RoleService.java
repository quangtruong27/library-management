package com.quangtruong.library_management.service.impl;

import com.quangtruong.library_management.dto.role.RoleRequest;
import com.quangtruong.library_management.dto.role.RoleResponse;
import com.quangtruong.library_management.entity.Permission;
import com.quangtruong.library_management.entity.Role;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.mapper.IRoleMapper;
import com.quangtruong.library_management.repository.IPermissionRepository;
import com.quangtruong.library_management.repository.IRoleRepository;
import com.quangtruong.library_management.service.IRoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService implements IRoleService {

	IRoleRepository roleRepository;
	IPermissionRepository permissionRepository;
	IRoleMapper roleMapper;

	@Override
	public Page<RoleResponse> getAll(Pageable pageable) {
		return roleRepository.findAll(pageable)
				.map(roleMapper::toResponse);
	}

	@Override
	public RoleResponse getById(Long id) {

		Role role = roleRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

		return roleMapper.toResponse(role);
	}

	@Override
	public RoleResponse create(RoleRequest request) {

		Role role = roleMapper.toEntity(request);

		// If request contains permissions ID array -> Query all Permissions from DB and assign to Role
		if (request.getPermissions() != null && !request.getPermissions().isEmpty()) {

			List<Permission> permissions = permissionRepository
					.findAllById(request.getPermissions());

			role.setPermissions(new HashSet<>(permissions));
		}

		role = roleRepository.save(role);
		return roleMapper.toResponse(role);
	}

	@Override
	public RoleResponse update(Long id, RoleRequest request) {
		Role role = roleRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

		roleMapper.update(role, request);

		if (request.getPermissions() != null) {

			List<Permission> permissions = permissionRepository
					.findAllById(request.getPermissions());

			role.setPermissions(new HashSet<>(permissions));
		}

		role = roleRepository.save(role);
		return roleMapper.toResponse(role);
	}

	@Override
	public void delete(Long id) {

		roleRepository.deleteById(id);
	}
}
