package com.quangtruong.library_management.service.impl;

import com.quangtruong.library_management.dto.incident.*;
import com.quangtruong.library_management.entity.Incident;
import com.quangtruong.library_management.entity.IncidentComment;
import com.quangtruong.library_management.entity.User;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.repository.IIncidentCommentRepository;
import com.quangtruong.library_management.repository.IIncidentRepository;
import com.quangtruong.library_management.repository.IUserRepository;
import com.quangtruong.library_management.service.IIncidentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IncidentService implements IIncidentService {

	IIncidentRepository incidentRepository;
	IIncidentCommentRepository incidentCommentRepository;
	IUserRepository userRepository;

	private IncidentCommentResponse mapToCommentResponse(IncidentComment comment) {
		return IncidentCommentResponse.builder()
				.id(comment.getId())
				.userId(comment.getUser().getId())
				.userName(comment.getUser().getName())
				.content(comment.getContent())
				.createdDate(comment.getCreatedDate())
				.build();
	}

	private IncidentResponse mapToResponse(Incident incident) {
		UUID assigneeId = incident.getAssignee() != null ? incident.getAssignee().getId() : null;
		String assigneeName = incident.getAssignee() != null ? incident.getAssignee().getName() : null;

		List<IncidentCommentResponse> comments = incident.getComments() != null ? incident.getComments().stream()
				.map(this::mapToCommentResponse)
				.collect(Collectors.toList()) : List.of();

		return IncidentResponse.builder()
				.id(incident.getId())
				.title(incident.getTitle())
				.description(incident.getDescription())
				.priority(incident.getPriority())
				.status(incident.getStatus())
				.reporterId(incident.getReporter().getId())
				.reporterName(incident.getReporter().getName())
				.assigneeId(assigneeId)
				.assigneeName(assigneeName)
				.resolutionNote(incident.getResolutionNote())
				.createdDate(incident.getCreatedDate())
				.updatedDate(incident.getUpdatedDate())
				.comments(comments)
				.build();
	}

	@Override
	public Page<IncidentResponse> getAllIncidents(String status, String priority, Pageable pageable) {
		boolean isStudent = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));

		UUID reporterId = null;
		if (isStudent) {
			String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
			reporterId = UUID.fromString(currentUserId);
		}

		return incidentRepository.searchIncidents(status, priority, reporterId, pageable)
				.map(this::mapToResponse);
	}

	@Override
	@Transactional
	public IncidentResponse createIncident(IncidentCreateRequest request, UUID reporterUserId) {
		User reporter = userRepository.findById(reporterUserId)
				.orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

		Incident incident = Incident.builder()
				.title(request.getTitle())
				.description(request.getDescription())
				.priority(request.getPriority())
				.status("OPEN")
				.reporter(reporter)
				.createdDate(LocalDateTime.now())
				.updatedDate(LocalDateTime.now())
				.build();

		incidentRepository.save(incident);
		return mapToResponse(incident);
	}

	@Override
	@Transactional
	public IncidentResponse updateIncident(Long incidentId, IncidentUpdateRequest request) {
		Incident incident = incidentRepository.findById(incidentId)
				.orElseThrow(() -> new AppException(ErrorCode.INCIDENT_NOT_FOUND));

		if (request.getStatus() != null) {
			incident.setStatus(request.getStatus());
		}

		if (request.getAssignee() != null && request.getAssignee().getId() != null) {
			User assignee = userRepository.findById(request.getAssignee().getId())
					.orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));
			incident.setAssignee(assignee);
		}

		if (request.getResolutionNote() != null) {
			incident.setResolutionNote(request.getResolutionNote());
		}

		incident.setUpdatedDate(LocalDateTime.now());
		incidentRepository.save(incident);
		return mapToResponse(incident);
	}

	@Override
	public IncidentResponse getIncidentById(Long incidentId) {
		Incident incident = incidentRepository.findById(incidentId)
				.orElseThrow(() -> new AppException(ErrorCode.INCIDENT_NOT_FOUND));

		boolean isStudent = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
		if (isStudent) {
			String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
			if (!incident.getReporter().getId().equals(UUID.fromString(currentUserId))) {
				throw new AppException(ErrorCode.RESERVATION_NOT_OWNED);
			}
		}

		return mapToResponse(incident);
	}

	@Override
	@Transactional
	public IncidentResponse closeIncident(Long incidentId) {
		Incident incident = incidentRepository.findById(incidentId)
				.orElseThrow(() -> new AppException(ErrorCode.INCIDENT_NOT_FOUND));

		incident.setStatus("CLOSED");
		incident.setUpdatedDate(LocalDateTime.now());
		incidentRepository.save(incident);

		String currentUserIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
		User sysUser = userRepository.findById(UUID.fromString(currentUserIdStr)).orElse(null);

		if (sysUser != null) {
			IncidentComment closeComment = IncidentComment.builder()
					.incident(incident)
					.user(sysUser)
					.content("System Notification: Incident has been closed.")
					.createdDate(LocalDateTime.now())
					.build();
			incidentCommentRepository.save(closeComment);
		}

		return mapToResponse(incident);
	}

	@Override
	@Transactional
	public IncidentCommentResponse addComment(Long incidentId, IncidentCommentRequest request, UUID userId) {
		Incident incident = incidentRepository.findById(incidentId)
				.orElseThrow(() -> new AppException(ErrorCode.INCIDENT_NOT_FOUND));

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

		IncidentComment comment = IncidentComment.builder()
				.incident(incident)
				.user(user)
				.content(request.getContent())
				.createdDate(LocalDateTime.now())
				.build();

		incidentCommentRepository.save(comment);
		return mapToCommentResponse(comment);
	}
}
