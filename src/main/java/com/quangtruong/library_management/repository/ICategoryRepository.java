package com.quangtruong.library_management.repository;

import com.quangtruong.library_management.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICategoryRepository extends JpaRepository<Category, Long> {
	Page<Category> findAll(Pageable pageable);
	boolean existsByName(String name);
}
