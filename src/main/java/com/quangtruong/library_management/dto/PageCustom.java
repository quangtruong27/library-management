package com.quangtruong.library_management.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageCustom<T> {
	int size;
	int pageNumber;
	long totalElements;
	int totalPages;

	public PageCustom(Page<T> page) {
		this.size = page.getSize();
		this.pageNumber = page.getNumber();
		this.totalElements =  page.getTotalElements();
		this.totalPages = page.getTotalPages();
	}
}
