package com.quangtruong.library_management.service.impl;

import com.quangtruong.library_management.dto.booklocation.BookLocationRequest;
import com.quangtruong.library_management.dto.booklocation.BookLocationResponse;
import com.quangtruong.library_management.entity.BookLocation;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.mapper.IBookLocationMapper;
import com.quangtruong.library_management.repository.IBookLocationRepository;
import com.quangtruong.library_management.service.IBookLocationService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookLocationService implements IBookLocationService {

	IBookLocationRepository bookLocationRepository;
	IBookLocationMapper bookLocationMapper;

	@Override
	public BookLocationResponse getById(Long id) {
		BookLocation location = bookLocationRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.BOOK_LOCATION_NOT_FOUND));
		return bookLocationMapper.toResponse(location);
	}

	@Override
	public Page<BookLocationResponse> getAll(Pageable pageable) {
		return bookLocationRepository.findAll(pageable).map(bookLocationMapper::toResponse);
	}

	@Override
	@Transactional
	public BookLocationResponse create(BookLocationRequest request) {
		if (bookLocationRepository.existsByName(request.getName())) {
			throw new AppException(ErrorCode.BOOK_LOCATION_ALREADY_EXISTS);
		}
		BookLocation location = bookLocationMapper.toEntity(request);
		location = bookLocationRepository.save(location);
		return bookLocationMapper.toResponse(location);
	}
	@Override
	@Transactional
	public BookLocationResponse update(Long id, BookLocationRequest request) {
		BookLocation location = bookLocationRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.BOOK_LOCATION_NOT_FOUND));
		if (!location.getName().equals(request.getName()) && bookLocationRepository.existsByName(request.getName())) {
			throw new AppException(ErrorCode.BOOK_LOCATION_ALREADY_EXISTS);
		}
		bookLocationMapper.update(location, request);
		location = bookLocationRepository.save(location);
		return bookLocationMapper.toResponse(location);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		BookLocation location = bookLocationRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.BOOK_LOCATION_NOT_FOUND));
		bookLocationRepository.delete(location);
	}
}