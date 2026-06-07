package com.quangtruong.library_management.dto.fine;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FineBalanceResponse {
	UUID studentId;
	String studentName;
	String studentCode;
	BigDecimal balance;
}
