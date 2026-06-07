package com.quangtruong.library_management.repository;

import com.quangtruong.library_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IUserRepository extends JpaRepository<User, UUID> {
		Optional<User> findByEmail(String email);

}
