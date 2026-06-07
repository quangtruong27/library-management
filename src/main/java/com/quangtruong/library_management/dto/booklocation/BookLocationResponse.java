package com.quangtruong.library_management.dto.booklocation;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookLocationResponse {
	Long id;
	String name;
	String description;
}
