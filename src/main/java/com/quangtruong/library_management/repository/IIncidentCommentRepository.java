package com.quangtruong.library_management.repository;

import com.quangtruong.library_management.entity.IncidentComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IIncidentCommentRepository extends JpaRepository<IncidentComment, Long> {
	List<IncidentComment> findByIncidentIdOrderByCreatedDateAsc(Long incidentId);
}
