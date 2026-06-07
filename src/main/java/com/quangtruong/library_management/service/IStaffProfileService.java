package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.staff.StaffProfileRequest;
import com.quangtruong.library_management.entity.StaffProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IStaffProfileService {
	Page<StaffProfile> getAll(Pageable pageable);
	StaffProfile createStaff(StaffProfileRequest staffProfile);
	StaffProfile updateStaff(UUID id, StaffProfileRequest request);
	void deleteStaff(UUID id);
}
