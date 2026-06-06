package com.quangtruong.library_management.dto.staff;

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
public class StaffProfileResponse {

	UUID userId;
	String name;
	String email;
	String status;
	String position;

	String employeeCode;
	String phone;
	Gender gender;
	LocalDate dob;
	String address;
	String avatarUrl;

}
