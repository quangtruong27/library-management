package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.role.RoleRequest;
import com.quangtruong.library_management.dto.role.RoleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IRoleService {
	Page<RoleResponse> getAll(Pageable pageable);
	RoleResponse getById(Long id);
	RoleResponse create(RoleRequest request);
	RoleResponse update(Long id, RoleRequest request);
	void delete(Long id);
}
