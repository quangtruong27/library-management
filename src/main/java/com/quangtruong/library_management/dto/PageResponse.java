package com.quangtruong.library_management.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<T> {
	PageCustom<T> pageable;
	List<T> content;

	public PageResponse(Page<T> page){
		this.pageable = new PageCustom<>(page);
		this.content = page.getContent();
	}
}

