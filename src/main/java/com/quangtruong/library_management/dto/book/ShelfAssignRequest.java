package com.quangtruong.library_management.dto.book;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShelfAssignRequest {
	@NotNull(message = "ID vị trí kệ không được để trống")
	Long locationId;
}
