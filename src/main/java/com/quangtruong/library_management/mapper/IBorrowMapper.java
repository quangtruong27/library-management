package com.quangtruong.library_management.mapper;

import com.quangtruong.library_management.dto.borrow.BorrowDetailResponse;
import com.quangtruong.library_management.dto.borrow.BorrowResponse;
import com.quangtruong.library_management.entity.Borrow;
import com.quangtruong.library_management.entity.BorrowDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
		componentModel = "spring",
		unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IBorrowMapper {

	@Mapping(source = "studentProfile.id", target = "studentId")
	@Mapping(source = "studentProfile.user.name", target = "studentName")
	@Mapping(source = "studentProfile.studentCode", target = "studentCode")
	@Mapping(source = "staffProfile.id", target = "staffId")
	@Mapping(source = "staffProfile.user.name", target = "staffName")
	@Mapping(source = "borrowDetai", target = "details")
	BorrowResponse toResponse(Borrow borrow);

	@Mapping(source = "bookCopy.id", target = "bookCopyId")
	@Mapping(source = "bookCopy.qrCode", target = "bookCopyQrCode")
	@Mapping(source = "bookCopy.book.name", target = "bookName")
	BorrowDetailResponse toResponseDetail(BorrowDetail borrowDetail);
}
