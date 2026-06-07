package com.quangtruong.library_management.repository;

import com.quangtruong.library_management.entity.Incident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IIncidentRepository extends JpaRepository<Incident, Long> {

	@Query("SELECT i FROM Incident i WHERE " +
			"(:status IS NULL OR i.status = :status) AND " +
			"(:priority IS NULL OR i.priority = :priority) AND " +
			"(:reporterId IS NULL OR i.reporter.id = :reporterId)")
	Page<Incident> searchIncidents(@Param("status") String status,
								   @Param("priority") String priority,
								   @Param("reporterId") UUID reporterId,
								   Pageable pageable);
}
