package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.booklocation.BookLocationRequest;
import com.quangtruong.library_management.dto.booklocation.BookLocationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBookLocationService {
	BookLocationResponse getById(Long id);
	Page<BookLocationResponse> getAll(Pageable pageable);
	BookLocationResponse create(BookLocationRequest request);
	BookLocationResponse update(Long id, BookLocationRequest request);
	void delete(Long id);
}
