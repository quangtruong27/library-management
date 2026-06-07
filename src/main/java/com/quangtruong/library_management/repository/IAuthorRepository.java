package com.quangtruong.library_management.repository;

import com.quangtruong.library_management.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IAuthorRepository extends JpaRepository<Author, Long> {
	Page<Author> findAll(Pageable pageable);
	boolean existsByName(String name);
}
