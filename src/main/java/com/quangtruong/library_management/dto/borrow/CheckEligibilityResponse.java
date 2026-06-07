package com.quangtruong.library_management.dto.borrow;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckEligibilityResponse {
	boolean eligible;
	List<String> reasons;
}
