package com.quangtruong.library_management.repository;

import com.quangtruong.library_management.entity.StaffPosition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IStaffPositionRepository extends JpaRepository<StaffPosition, Long> {
}
