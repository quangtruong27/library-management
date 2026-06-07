package com.quangtruong.library_management.dto.publisher;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublisherResponse {
	Long id;
	String name;
	String email;
	String contact;
}
