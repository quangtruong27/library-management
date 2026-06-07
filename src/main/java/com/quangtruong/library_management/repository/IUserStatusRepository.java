package com.quangtruong.library_management.repository;

import com.quangtruong.library_management.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserStatusRepository extends JpaRepository<UserStatus, Long> {
	Optional<UserStatus> findByName(String name);
}
