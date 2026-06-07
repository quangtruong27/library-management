package com.quangtruong.library_management.repository;

import com.quangtruong.library_management.entity.Clazz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IClazzRepository extends JpaRepository<Clazz, Long> {
	boolean existsByName(String name);
}
