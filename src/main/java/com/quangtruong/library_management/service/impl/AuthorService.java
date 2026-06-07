package com.quangtruong.library_management.service.impl;

import com.quangtruong.library_management.dto.author.AuthorRequest;
import com.quangtruong.library_management.dto.author.AuthorResponse;
import com.quangtruong.library_management.entity.Author;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.mapper.IAuthorMapper;
import com.quangtruong.library_management.repository.IAuthorRepository;
import com.quangtruong.library_management.service.IAuthorService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthorService implements IAuthorService {
	IAuthorRepository authorRepository;
	IAuthorMapper authorMapper;

	@Override
	public AuthorResponse getById(Long id) {
		Author author = authorRepository.findById(id).
				orElseThrow(() -> new AppException(ErrorCode.AUTHOR_NOT_FOUND));

		return authorMapper.toResponse(author);
	}

	@Override
	public Page<AuthorResponse> getAll(Pageable pageable) {

		return authorRepository.findAll(pageable)
				.map(authorMapper::toResponse);
	}

	@Override
	public AuthorResponse createAuthor(AuthorRequest authorRequest) {
		if (authorRepository.existsByName(authorRequest.getName())) {
			throw new AppException(ErrorCode.AUTHOR_ALREADY_EXISTS);
		}

		Author author = authorMapper.toEntity(authorRequest);

		return authorMapper.toResponse(authorRepository.save(author));
	}

	@Override
	public AuthorResponse updateAuthor(Long id, AuthorRequest authorRequest) {

		Author author = authorRepository.findById(id).
				orElseThrow(() -> new AppException(ErrorCode.AUTHOR_NOT_FOUND));

		// check name trung
		if (!author.getName().equals(authorRequest.getName())
				&& authorRepository.existsByName(authorRequest.getName())) {
			throw new AppException(ErrorCode.AUTHOR_ALREADY_EXISTS);
		}

		authorMapper.update(author, authorRequest);

		return authorMapper.toResponse(authorRepository.save(author));
	}

	@Override
	public void deleteAuthor(Long id) {
		Author author = authorRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.AUTHOR_NOT_FOUND));
		authorRepository.delete(author);
	}

}
