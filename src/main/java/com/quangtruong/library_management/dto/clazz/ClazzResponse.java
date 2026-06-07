package com.quangtruong.library_management.dto.clazz;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClazzResponse {
	Long id;
	String name;
	String courseYear;

	Long facultyId;
	String facultyName;
}
