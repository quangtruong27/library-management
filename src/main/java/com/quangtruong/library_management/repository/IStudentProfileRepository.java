package com.quangtruong.library_management.repository;

import com.quangtruong.library_management.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IStudentProfileRepository extends JpaRepository<StudentProfile, UUID> {
	boolean existsByStudentCode(String studentCode);
}
