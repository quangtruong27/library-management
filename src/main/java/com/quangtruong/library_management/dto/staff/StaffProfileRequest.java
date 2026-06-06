package com.quangtruong.library_management.dto.staff;

import com.quangtruong.library_management.entity.Gender;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StaffProfileRequest {
	@NotBlank(message = "Tên không được để trống")
	String name;

	@NotBlank(message = "Email không được để trống")
	@Email(message = "Email không đúng định dạng")
	String email;

	@NotBlank(message = "Mật khẩu không được để trống")
	@Size(min = 6, message = "Mật khẩu phải cso ít nhất 6 kí tự")
	String password;

	@NotBlank(message = "Mã nhân viên không được để trống")
	String employeeCode;

	@NotBlank(message = "Số  thoại không được để trống")
	@Size(max = 10, message = "Số điện thoạt tối đa 10 số")
	String phone;

	@NotNull(message = "Giới tính không được để trống")
	Gender gender;

	@PastOrPresent(message = "Ngày sinh không được ở trong tương lai")
	LocalDate dob;

	@NotNull(message = "Trạng thái không được để trống")
	Long statusId;

	@NotNull(message = "Chức vụ không được để trống")
	StaffPositionIdRequest position;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class StaffPositionIdRequest {
		Long id;
	}

	String address;
	String avatarUrl;

}
