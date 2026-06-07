package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.clazz.ClazzRequest;
import com.quangtruong.library_management.dto.clazz.ClazzResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IClazzService {
	Page<ClazzResponse> getAll(Pageable pageable);
	ClazzResponse getById(Long id);
	ClazzResponse create(ClazzRequest request);
	ClazzResponse update(Long id, ClazzRequest request);
	void delete(Long id);
}
