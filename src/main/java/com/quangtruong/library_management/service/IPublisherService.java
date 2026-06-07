package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.publisher.PublisherRequest;
import com.quangtruong.library_management.dto.publisher.PublisherResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPublisherService {
	Page<PublisherResponse> getAll(Pageable pageable);
	PublisherResponse getById(Long id);
	PublisherResponse create(PublisherRequest request);
	PublisherResponse update(Long id, PublisherRequest request);
	void delete(Long id);
}
