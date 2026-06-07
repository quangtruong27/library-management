package com.quangtruong.library_management.repository;

import com.quangtruong.library_management.entity.BookLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBookLocationRepository extends JpaRepository<BookLocation, Long> {
	boolean existsByName(String name);
}
