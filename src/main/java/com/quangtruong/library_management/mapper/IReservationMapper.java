package com.quangtruong.library_management.mapper;

import com.quangtruong.library_management.dto.reservation.ReservationResponse;
import com.quangtruong.library_management.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
		componentModel = "spring",
		unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IReservationMapper {

	@Mapping(source = "status.id",   target = "statusId")
	@Mapping(source = "status.name", target = "statusName")
	@Mapping(source = "bookCopy.id",         target = "bookCopyId")
	@Mapping(source = "bookCopy.book.name",  target = "bookName")
	@Mapping(source = "bookCopy.qrCode",     target = "bookQrCode")
	@Mapping(source = "studentProfile.studentCode", target = "studentCode")
	@Mapping(source = "studentProfile.user.name",   target = "studentName")
	ReservationResponse toResponse(Reservation reservation);
}
