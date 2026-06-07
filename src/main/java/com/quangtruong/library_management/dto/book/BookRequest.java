package com.quangtruong.library_management.dto.book;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookRequest {

	@NotBlank(message = "Tên sách không được để trống")
	@Size(max = 255, message = "Tên sách tối đa 255 ký tự")
	String name;

	@Size(max = 2000, message = "Mô tả tối đa 2000 ký tự")
	String description;

	@NotNull(message = "Giá sách không được để trống")
	@DecimalMin(value = "0.0", inclusive = false, message = "Giá sách phải lớn hơn 0")
	@Digits(integer = 10, fraction = 2, message = "Giá sách không hợp lệ")
	BigDecimal price;

	@NotNull(message = "Năm xuất bản không được để trống")
	@Min(value = 1000, message = "Năm xuất bản không hợp lệ")
	@Max(value = 2100, message = "Năm xuất bản không hợp lệ")
	Integer publisherYear;

	@NotNull(message = "Danh mục không được để trống")
	CategoryIdRequest category;

	@NotNull(message = "Nhà xuất bản không được để trống")
	PublisherIdRequest publisher;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CategoryIdRequest {
		Long id;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PublisherIdRequest {
		Long id;
	}

	@NotNull(message = "Số lượng bản in không được để trống")
	@Min(value = 1, message = "Số lượng bản in phải lớn hơn 0")
	Integer numberOfCopies;

	@NotEmpty(message = "Phải có ít nhất 1 tác giả")
	Set<Long> authorId;
}