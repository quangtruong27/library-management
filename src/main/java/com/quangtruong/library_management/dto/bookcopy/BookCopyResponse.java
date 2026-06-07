package com.quangtruong.library_management.dto.bookcopy;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookCopyResponse {
	Long id;
	String qrCode;
	String status;
	String locationName;
}
