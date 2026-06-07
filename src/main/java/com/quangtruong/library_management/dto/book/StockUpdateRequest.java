package com.quangtruong.library_management.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockUpdateRequest {
	@NotBlank(message = "Hành động không được để trống (VD: ADD)")
	String action; // e.g., "ADD" to import more books
	@NotNull(message = "Số lượng không được để trống")
	@Min(value = 1, message = "Số lượng phải lớn hơn 0")
	Integer quantity;
}
