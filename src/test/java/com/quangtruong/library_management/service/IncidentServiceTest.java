package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.incident.*;
import com.quangtruong.library_management.entity.Incident;
import com.quangtruong.library_management.entity.IncidentComment;
import com.quangtruong.library_management.entity.User;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.repository.IIncidentCommentRepository;
import com.quangtruong.library_management.repository.IIncidentRepository;
import com.quangtruong.library_management.repository.IUserRepository;
import com.quangtruong.library_management.service.impl.IncidentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IncidentServiceTest {

	@Mock
	IIncidentRepository incidentRepository;

	@Mock
	IIncidentCommentRepository incidentCommentRepository;

	@Mock
	IUserRepository userRepository;

	@InjectMocks
	IncidentService incidentService;

	@Mock
	SecurityContext securityContext;

	@Mock
	Authentication authentication;

	UUID userId = UUID.randomUUID();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		when(authentication.getName()).thenReturn(userId.toString());
	}

	@Test
	void createIncident_Success() {
		IncidentCreateRequest request = IncidentCreateRequest.builder()
				.title("Test Ticket")
				.description("Test Desc")
				.priority("HIGH")
				.build();

		User reporter = User.builder().id(userId).name("Test User").build();
		when(userRepository.findById(userId)).thenReturn(Optional.of(reporter));

		Incident savedIncident = Incident.builder()
				.id(1L)
				.title(request.getTitle())
				.description(request.getDescription())
				.priority(request.getPriority())
				.status("OPEN")
				.reporter(reporter)
				.createdDate(LocalDateTime.now())
				.updatedDate(LocalDateTime.now())
				.comments(new ArrayList<>())
				.build();

		when(incidentRepository.save(any(Incident.class))).thenReturn(savedIncident);

		IncidentResponse response = incidentService.createIncident(request, userId);

		assertNotNull(response);
		assertEquals("Test Ticket", response.getTitle());
		assertEquals("OPEN", response.getStatus());
		verify(incidentRepository, times(1)).save(any(Incident.class));
	}

	@Test
	void createIncident_UserNotFound() {
		IncidentCreateRequest request = IncidentCreateRequest.builder()
				.title("Test Ticket")
				.description("Test Desc")
				.priority("HIGH")
				.build();

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		AppException exception = assertThrows(AppException.class, () -> {
			incidentService.createIncident(request, userId);
		});

		assertEquals(ErrorCode.UNAUTHORIZED, exception.getErrorCode());
	}

	@Test
	void getIncidentById_Success_Admin() {
		User reporter = User.builder().id(UUID.randomUUID()).name("Reporter").build();
		Incident incident = Incident.builder()
				.id(1L)
				.title("Bug")
				.description("Details")
				.priority("LOW")
				.status("OPEN")
				.reporter(reporter)
				.comments(new ArrayList<>())
				.build();

		when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));

		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		doReturn(authorities).when(authentication).getAuthorities();

		IncidentResponse response = incidentService.getIncidentById(1L);

		assertNotNull(response);
		assertEquals("Bug", response.getTitle());
	}

	@Test
	void getIncidentById_Forbidden_StudentNotOwner() {
		User reporter = User.builder().id(UUID.randomUUID()).name("Reporter").build();
		Incident incident = Incident.builder()
				.id(1L)
				.title("Bug")
				.description("Details")
				.priority("LOW")
				.status("OPEN")
				.reporter(reporter)
				.build();

		when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));

		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_STUDENT"));
		doReturn(authorities).when(authentication).getAuthorities();

		AppException exception = assertThrows(AppException.class, () -> {
			incidentService.getIncidentById(1L);
		});

		assertEquals(ErrorCode.RESERVATION_NOT_OWNED, exception.getErrorCode());
	}

	@Test
	void closeIncident_Success() {
		User reporter = User.builder().id(userId).name("Reporter").build();
		Incident incident = Incident.builder()
				.id(1L)
				.title("Bug")
				.description("Details")
				.priority("LOW")
				.status("OPEN")
				.reporter(reporter)
				.comments(new ArrayList<>())
				.build();

		when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));
		when(userRepository.findById(userId)).thenReturn(Optional.of(reporter));

		IncidentResponse response = incidentService.closeIncident(1L);

		assertNotNull(response);
		assertEquals("CLOSED", response.getStatus());
		verify(incidentCommentRepository, times(1)).save(any(IncidentComment.class));
	}

	@Test
	void addComment_Success() {
		User reporter = User.builder().id(userId).name("Commenter").build();
		Incident incident = Incident.builder()
				.id(1L)
				.title("Bug")
				.description("Details")
				.reporter(reporter)
				.build();

		IncidentCommentRequest request = IncidentCommentRequest.builder().content("Check this please").build();

		when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));
		when(userRepository.findById(userId)).thenReturn(Optional.of(reporter));

		IncidentCommentResponse response = incidentService.addComment(1L, request, userId);

		assertNotNull(response);
		assertEquals("Check this please", response.getContent());
		verify(incidentCommentRepository, times(1)).save(any(IncidentComment.class));
	}
}
