package com.quangtruong.library_management.dto.student;

import com.quangtruong.library_management.entity.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentProfileRequest {
	@NotBlank(message = "Họ tên không được để trống")
	String name;

	@Email(message = "Email không hợp lệ")
	String email;

	@NotBlank(message = "Mật khẩu không được để trống")
	String password;

	@NotBlank(message = "Mã sinh viên không được để trống")
	String studentCode;

	Long statusId;
	ClazzIdRequest clazz;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ClazzIdRequest {
		Long id;
	}

	String phone;
	Gender gender;
	LocalDate dob;
	String address;
	Integer enrollmentYear;
}
