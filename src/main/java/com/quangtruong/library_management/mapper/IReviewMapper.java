package com.quangtruong.library_management.mapper;

import com.quangtruong.library_management.dto.review.ReviewRequest;
import com.quangtruong.library_management.dto.review.ReviewResponse;
import com.quangtruong.library_management.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
		componentModel = "spring",
		unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IReviewMapper {

	@Mapping(target = "book", ignore = true)
	@Mapping(target = "studentProfile", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	Review toEntity(ReviewRequest request);

	@Mapping(source = "book.id",   target = "bookId")
	@Mapping(source = "book.name", target = "bookName")
	@Mapping(source = "studentProfile.studentCode", target = "studentCode")
	@Mapping(source = "studentProfile.user.name",   target = "studentName")
	ReviewResponse toResponse(Review review);

	@Mapping(target = "book", ignore = true)
	@Mapping(target = "studentProfile", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	void update(@MappingTarget Review review, ReviewRequest request);
}
