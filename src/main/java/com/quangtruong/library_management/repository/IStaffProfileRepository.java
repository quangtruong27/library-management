package com.quangtruong.library_management.repository;

import com.quangtruong.library_management.entity.StaffProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IStaffProfileRepository extends JpaRepository<StaffProfile, UUID> {
	Page<StaffProfile> findAll(Pageable pageable);
	Optional<StaffProfile> findByUserId(UUID userId);

}
