package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.bookcopy.BookCopyDetailResponse;
import com.quangtruong.library_management.dto.bookcopy.BookCopyResponse;
import com.quangtruong.library_management.dto.bookcopy.BookCopyStatusUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBookCopyService {
	Page<BookCopyResponse> getBookCopies(Long bookId, String status, Pageable pageable);
	BookCopyDetailResponse getBookCopyDetails(Long copyId);
	void updateCirculationStatus(Long copyId, BookCopyStatusUpdateRequest request);
	void deleteBookCopy(Long copyId);
}
