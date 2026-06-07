package com.quangtruong.library_management.service.impl;

import com.quangtruong.library_management.dto.bookcopy.BookCopyResponse;
import com.quangtruong.library_management.dto.bookcopy.BookCopyDetailResponse;
import com.quangtruong.library_management.dto.bookcopy.BookCopyStatusUpdateRequest;
import com.quangtruong.library_management.dto.borrow.BorrowHistoryResponse;
import com.quangtruong.library_management.entity.BookCopy;
import com.quangtruong.library_management.entity.BorrowDetail;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.mapper.IBookCopyMapper;
import com.quangtruong.library_management.repository.IBookCopyRepository;
import com.quangtruong.library_management.repository.IBorrowDetailRepository;
import com.quangtruong.library_management.service.IBookCopyService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookCopyService implements IBookCopyService {

	IBookCopyRepository bookCopyRepository;
	IBorrowDetailRepository borrowDetailRepository;
	IBookCopyMapper bookCopyMapper;

	@Override
	public Page<BookCopyResponse> getBookCopies(Long bookId, String status, Pageable pageable) {
		return bookCopyRepository.findCopies(bookId, status, pageable)
				.map(bookCopyMapper::toResponse);
	}

	@Override
	public BookCopyDetailResponse getBookCopyDetails(Long copyId) {
		BookCopy copy = bookCopyRepository.findById(copyId)
				.orElseThrow(() -> new AppException(ErrorCode.BOOK_COPY_NOT_FOUND));

		List<BorrowDetail> borrowDetails = borrowDetailRepository.findByBookCopyIdOrderByBorrowBorrowDateDesc(copyId);

		List<BorrowHistoryResponse> history = borrowDetails.stream().map(bd -> BorrowHistoryResponse.builder()
				.borrowId(bd.getBorrow().getId())
				.borrowDate(bd.getBorrow().getBorrowDate())
				.dueDate(bd.getBorrow().getDueDate())
				.returnDate(bd.getReturnDate())
				.note(bd.getNote())
				.studentName(bd.getBorrow().getStudentProfile() != null && bd.getBorrow().getStudentProfile().getUser() != null ?
						bd.getBorrow().getStudentProfile().getUser().getName() : "Unknown")
				.studentCode(bd.getBorrow().getStudentProfile() != null ?
						bd.getBorrow().getStudentProfile().getStudentCode() : "Unknown")
				.build()).collect(Collectors.toList());

		return BookCopyDetailResponse.builder()
				.id(copy.getId())
				.qrCode(copy.getQrCode())
				.status(copy.getStatus())
				.bookName(copy.getBook() != null ? copy.getBook().getName() : null)
				.locationName(copy.getBookLocation() != null ? copy.getBookLocation().getName() : null)
				.borrowHistory(history)
				.build();
	}

	@Override
	@Transactional
	public void updateCirculationStatus(Long copyId, BookCopyStatusUpdateRequest request) {
		BookCopy copy = bookCopyRepository.findById(copyId)
				.orElseThrow(() -> new AppException(ErrorCode.BOOK_COPY_NOT_FOUND));
		copy.setStatus(request.getStatus());
		bookCopyRepository.save(copy);
	}

	@Override
	@Transactional
	public void deleteBookCopy(Long copyId) {
		BookCopy copy = bookCopyRepository.findById(copyId)
				.orElseThrow(() -> new AppException(ErrorCode.BOOK_COPY_NOT_FOUND));
		bookCopyRepository.delete(copy);
	}
}
