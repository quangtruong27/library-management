package com.quangtruong.library_management.repository;

import com.quangtruong.library_management.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPublisherRepository extends JpaRepository<Publisher, Long> {
}
