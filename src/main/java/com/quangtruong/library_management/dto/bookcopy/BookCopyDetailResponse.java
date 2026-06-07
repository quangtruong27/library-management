package com.quangtruong.library_management.dto.bookcopy;
/*
Used for View Book Copy Detail API, including book name and borrow/return history
 */

import com.quangtruong.library_management.dto.borrow.BorrowHistoryResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookCopyDetailResponse {
	Long id;
	String qrCode;
	String status;
	String locationName;
	String bookName;
	List<BorrowHistoryResponse> borrowHistory;
}
