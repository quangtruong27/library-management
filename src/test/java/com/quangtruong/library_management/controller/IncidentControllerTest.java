package com.quangtruong.library_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quangtruong.library_management.dto.incident.*;
import com.quangtruong.library_management.service.IIncidentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class IncidentControllerTest {

	MockMvc mockMvc;

	@Mock
	IIncidentService incidentService;

	@InjectMocks
	IncidentController incidentController;

	@Mock
	SecurityContext securityContext;

	@Mock
	Authentication authentication;

	ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		when(authentication.getName()).thenReturn(UUID.randomUUID().toString());

		mockMvc = MockMvcBuilders.standaloneSetup(incidentController)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.build();
	}

	@Test
	void getAllIncidents_Success() throws Exception {
		IncidentResponse incident = IncidentResponse.builder()
				.id(1L)
				.title("Test Bug")
				.description("Details")
				.status("OPEN")
				.build();

		when(incidentService.getAllIncidents(any(), any(), any())).thenReturn(new PageImpl<>(Collections.singletonList(incident)));

		mockMvc.perform(get("/api/incidents")
						.param("status", "OPEN")
						.param("page", "0")
						.param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].id").value(1L))
				.andExpect(jsonPath("$.data.content[0].title").value("Test Bug"))
				.andExpect(jsonPath("$.data.content[0].status").value("OPEN"));
	}

	@Test
	void createIncident_Success() throws Exception {
		IncidentCreateRequest request = IncidentCreateRequest.builder()
				.title("Hardware issue")
				.description("Keyboard broken")
				.priority("MEDIUM")
				.build();

		IncidentResponse response = IncidentResponse.builder()
				.id(1L)
				.title("Hardware issue")
				.description("Keyboard broken")
				.status("OPEN")
				.build();

		when(incidentService.createIncident(any(), any())).thenReturn(response);

		mockMvc.perform(post("/api/incidents")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.id").value(1L))
				.andExpect(jsonPath("$.data.title").value("Hardware issue"));
	}

	@Test
	void getIncidentById_Success() throws Exception {
		IncidentResponse response = IncidentResponse.builder()
				.id(1L)
				.title("Bug")
				.status("OPEN")
				.build();

		when(incidentService.getIncidentById(1L)).thenReturn(response);

		mockMvc.perform(get("/api/incidents/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(1L))
				.andExpect(jsonPath("$.data.title").value("Bug"));
	}

	@Test
	void closeIncident_Success() throws Exception {
		IncidentResponse response = IncidentResponse.builder()
				.id(1L)
				.title("Bug")
				.status("CLOSED")
				.build();

		when(incidentService.closeIncident(1L)).thenReturn(response);

		mockMvc.perform(post("/api/incidents/1/close"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.status").value("CLOSED"));
	}
}
