package com.quangtruong.library_management.dto.publisher;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublisherRequest {
	@NotBlank(message = "Tên nhà xuất bản không được để trống")
	String name;
	String email;
	String contact;
}
