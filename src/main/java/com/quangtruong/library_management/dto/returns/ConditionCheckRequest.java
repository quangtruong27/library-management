package com.quangtruong.library_management.dto.returns;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConditionCheckRequest {
	String condition; // NORMAL, DAMAGED_LIGHT, DAMAGED_MEDIUM, DAMAGED_HEAVY, LOST
	String note;
}
