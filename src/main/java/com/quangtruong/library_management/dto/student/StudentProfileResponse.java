package com.quangtruong.library_management.dto.student;

import com.quangtruong.library_management.entity.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentProfileResponse {
	UUID id;
	String name;
	String email;

	Long statusId;
	String statusName;

	String studentCode;
	String phone;
	Gender gender;
	LocalDate dob;
	String address;
	Integer enrollmentYear;
	String avatarUrl;

	Long clazzId;
	String clazzName;
}
