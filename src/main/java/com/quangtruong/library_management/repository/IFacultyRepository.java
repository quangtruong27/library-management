package com.quangtruong.library_management.repository;

import com.quangtruong.library_management.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFacultyRepository extends JpaRepository<Faculty, Long> {
	boolean existsByName(String name);
}
