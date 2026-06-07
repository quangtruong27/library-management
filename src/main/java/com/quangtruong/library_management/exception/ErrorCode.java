package com.quangtruong.library_management.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
	//AUTHENTICATE
	UNAUTHORIZED(40101, "User name or password is not correct!", HttpStatus.UNAUTHORIZED),

	//STAFF
	STAFF_NOT_FOUND(40001, "Staff not found!", HttpStatus.NOT_FOUND),
	STAFF_NOT_EXISTS(40002, "Staff is not existed!", HttpStatus.BAD_REQUEST),

	//CATEGORY
	CATEGORY_NOT_FOUND(40003, "Category not found!", HttpStatus.NOT_FOUND),
	CATEGORY_ALREADY_EXISTS(40004, "Category already exists!", HttpStatus.BAD_REQUEST),

	// AUTHOR
	AUTHOR_NOT_FOUND(40005, "Author not found!", HttpStatus.NOT_FOUND),
	AUTHOR_ALREADY_EXISTS(40007, "Author already exists!", HttpStatus.BAD_REQUEST),

	// USER STATUS
	USER_STATUS_NOT_FOUND(40008, "User status not found!", HttpStatus.NOT_FOUND),

	// STAFF POSITION
	STAFF_POSITION_NOT_FOUND(40009, "Staff position not found!", HttpStatus.NOT_FOUND),

	// BOOK LOCATION
	BOOK_LOCATION_NOT_FOUND(40010, "Book location not found!", HttpStatus.NOT_FOUND),
	BOOK_LOCATION_ALREADY_EXISTS(40011, "Book location already exists!", HttpStatus.BAD_REQUEST),

	// BOOK
	BOOK_NOT_FOUND(40012, "Book not found!", HttpStatus.NOT_FOUND),

	// BOOK COPY
	BOOK_COPY_NOT_FOUND(40013, "Book copy not found!", HttpStatus.NOT_FOUND),

	// PUBLISHER
	PUBLISHER_NOT_FOUND(40014, "Publisher not found!", HttpStatus.NOT_FOUND),

	// ROLE
	ROLE_NOT_FOUND(40015, "Role not found!", HttpStatus.NOT_FOUND),

	// STUDENT
	STUDENT_NOT_FOUND(40016, "Student not found!", HttpStatus.NOT_FOUND),
	STUDENT_ALREADY_EXISTS(40017, "Student already exists!", HttpStatus.BAD_REQUEST),

	//CLAZZ
	CLAZZ_NOT_FOUND(40018, "Clazz not found!", HttpStatus.NOT_FOUND),
	CLAZZ_NOT_EXISTS(40019, "Clazz already exists!", HttpStatus.BAD_REQUEST),
	CLAZZ_NAME_EXISTS(40020, "The class name already exists!", HttpStatus.BAD_REQUEST),

	// FACULTY
	FACULTY_NOT_FOUND(40021, "Faculty not found!", HttpStatus.NOT_FOUND),
	FACULTY_NAME_EXISTS(40022, "Faculty name already exists!", HttpStatus.BAD_REQUEST),

	// RESERVATION
	RESERVATION_NOT_FOUND(40023, "Reservation not found!", HttpStatus.NOT_FOUND),
	RESERVATION_LIMIT_EXCEEDED(40024, "Borrow limit exceeded!", HttpStatus.BAD_REQUEST),
	NO_COPY_AVAILABLE(40025, "No available copy for this book!", HttpStatus.BAD_REQUEST),
	RESERVATION_ALREADY_CANCELLED(40026, "Reservation is already cancelled or expired!", HttpStatus.BAD_REQUEST),
	RESERVATION_NOT_OWNED(40027, "You do not own this reservation!", HttpStatus.FORBIDDEN),
	RESERVATION_STATUS_NOT_FOUND(40028, "Reservation status not found!", HttpStatus.NOT_FOUND),

	// REVIEW
	REVIEW_NOT_FOUND(40029, "Review not found!", HttpStatus.NOT_FOUND),
	REVIEW_ALREADY_EXISTS(40030, "You have already reviewed this book!", HttpStatus.BAD_REQUEST),
	REVIEW_NOT_OWNED(40031, "You do not own this review!", HttpStatus.FORBIDDEN),
	REVIEW_RATING_INVALID(40032, "Rating must be between 1 and 5!", HttpStatus.BAD_REQUEST),


	// BORROW / LOAN
	BORROW_NOT_FOUND(40033, "Borrow record not found!", HttpStatus.NOT_FOUND),
	BORROW_LIMIT_EXCEEDED(40034, "Student borrow limit exceeded!", HttpStatus.BAD_REQUEST),
	STUDENT_HAS_UNPAID_FINES(40035, "Student has unpaid fines!", HttpStatus.BAD_REQUEST),
	BOOK_COPY_NOT_AVAILABLE(40036, "Book copy is not available for borrowing!", HttpStatus.BAD_REQUEST),
	BORROW_CANNOT_CANCEL(40037, "Cannot cancel this borrow record!", HttpStatus.BAD_REQUEST),
	INVALID_LOAN_STATUS(40038, "Invalid loan status!", HttpStatus.BAD_REQUEST),
	INVALID_ISSUE_TYPE(40039, "Invalid issue type! Must be LOST or DAMAGED.", HttpStatus.BAD_REQUEST),
	FINE_NOT_FOUND(40040, "Fine record not found!", HttpStatus.NOT_FOUND),
	INCIDENT_NOT_FOUND(40041, "Incident not found!", HttpStatus.NOT_FOUND),
	;
	int code;
	String message;
	HttpStatus status;
}
