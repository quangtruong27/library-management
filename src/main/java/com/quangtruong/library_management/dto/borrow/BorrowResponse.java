package com.quangtruong.library_management.dto.borrow;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowResponse {
	Long id;
	LocalDateTime borrowDate;
	LocalDateTime dueDate;
	String status;
	UUID studentId;
	String studentName;
	String studentCode;
	UUID staffId;
	String staffName;
	List<BorrowDetailResponse> details;
}
