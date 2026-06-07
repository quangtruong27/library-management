package com.quangtruong.library_management.dto.borrow;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowDetailResponse {
	Long id;
	LocalDateTime returnDate;
	String note;
	Long bookCopyId;
	String bookCopyQrCode;
	String bookName;
}
