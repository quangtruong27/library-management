package com.quangtruong.library_management.service;


import com.quangtruong.library_management.dto.author.AuthorRequest;
import com.quangtruong.library_management.dto.author.AuthorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IAuthorService {
	AuthorResponse getById(Long id);
	Page<AuthorResponse> getAll(Pageable pageable);
	AuthorResponse createAuthor(AuthorRequest authorRequest);
	AuthorResponse updateAuthor(Long id, AuthorRequest authorRequest);
	void deleteAuthor(Long id);

}
