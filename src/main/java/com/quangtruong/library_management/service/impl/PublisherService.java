package com.quangtruong.library_management.service.impl;

import com.quangtruong.library_management.dto.publisher.PublisherRequest;
import com.quangtruong.library_management.dto.publisher.PublisherResponse;
import com.quangtruong.library_management.entity.Publisher;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.mapper.IPublisherMapper;
import com.quangtruong.library_management.repository.IPublisherRepository;
import com.quangtruong.library_management.service.IPublisherService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublisherService implements IPublisherService {

	IPublisherRepository publisherRepository;
	IPublisherMapper publisherMapper;

	@Override
	public Page<PublisherResponse> getAll(Pageable pageable) {
		return publisherRepository.findAll(pageable).map(publisherMapper::toResponse);
	}

	@Override
	public PublisherResponse getById(Long id) {
		Publisher publisher = publisherRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.PUBLISHER_NOT_FOUND));
		return publisherMapper.toResponse(publisher);
	}

	@Override
	public PublisherResponse create(PublisherRequest request) {
		Publisher publisher = publisherMapper.toEntity(request);
		publisher = publisherRepository.save(publisher);
		return publisherMapper.toResponse(publisher);
	}

	@Override
	public PublisherResponse update(Long id, PublisherRequest request) {
		Publisher publisher = publisherRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.PUBLISHER_NOT_FOUND));
		publisherMapper.update(publisher, request);
		publisher = publisherRepository.save(publisher);
		return publisherMapper.toResponse(publisher);
	}

	@Override
	public void delete(Long id) {
		// Cannot delete publisher linked to books (will cause ForeignKey constraint violation)
		publisherRepository.deleteById(id);
	}
}
