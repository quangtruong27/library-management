package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.incident.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IIncidentService {
	Page<IncidentResponse> getAllIncidents(String status, String priority, Pageable pageable);
	IncidentResponse createIncident(IncidentCreateRequest request, UUID reporterUserId);
	IncidentResponse updateIncident(Long incidentId, IncidentUpdateRequest request);
	IncidentResponse getIncidentById(Long incidentId);
	IncidentResponse closeIncident(Long incidentId);
	IncidentCommentResponse addComment(Long incidentId, IncidentCommentRequest request, UUID userId);
}
