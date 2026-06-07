package com.quangtruong.library_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quangtruong.library_management.dto.authenticate.AuthenticationRequest;
import com.quangtruong.library_management.dto.authenticate.AuthenticationResponse;
import com.quangtruong.library_management.dto.introspect.IntrospectRequest;
import com.quangtruong.library_management.dto.introspect.IntrospectResponse;
import com.quangtruong.library_management.service.IAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticationControllerTest {

	MockMvc mockMvc;

	@Mock
	IAuthenticationService authenticationService;

	@InjectMocks
	AuthenticationController authenticationController;

	ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
	}

	@Test
	void login_Success() throws Exception {
		AuthenticationRequest request = AuthenticationRequest.builder()
				.email("test@gmail.com")
				.password("password")
				.build();

		AuthenticationResponse response = AuthenticationResponse.builder()
				.token("dummy-jwt-token")
				.build();

		when(authenticationService.login(any())).thenReturn(response);

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.token").value("dummy-jwt-token"));
	}

	@Test
	void introspect_Success() throws Exception {
		IntrospectRequest request = IntrospectRequest.builder()
				.token("dummy-jwt-token")
				.build();

		IntrospectResponse response = IntrospectResponse.builder()
				.isValid(true)
				.build();

		when(authenticationService.introspect(any())).thenReturn(response);

		mockMvc.perform(post("/api/auth/introspect")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.valid").value(true));
	}
}
