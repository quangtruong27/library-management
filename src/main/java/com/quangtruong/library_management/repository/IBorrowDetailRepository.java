package com.quangtruong.library_management.repository;

import com.quangtruong.library_management.entity.BorrowDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IBorrowDetailRepository extends JpaRepository<BorrowDetail, Long> {
	List<BorrowDetail> findByBookCopyIdOrderByBorrowBorrowDateDesc(Long bookCopyId);
}
