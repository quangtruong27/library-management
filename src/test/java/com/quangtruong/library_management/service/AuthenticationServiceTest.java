package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.authenticate.AuthenticationRequest;
import com.quangtruong.library_management.dto.authenticate.AuthenticationResponse;
import com.quangtruong.library_management.dto.introspect.IntrospectRequest;
import com.quangtruong.library_management.dto.introspect.IntrospectResponse;
import com.quangtruong.library_management.entity.User;
import com.quangtruong.library_management.entity.UserStatus;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.repository.IUserRepository;
import com.quangtruong.library_management.service.impl.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AuthenticationServiceTest {

	@Mock
	IUserRepository userRepository;

	@InjectMocks
	AuthenticationService authenticationService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(authenticationService, "SIGNER_KEY", "12345678901234567890123456789012");
	}

	@Test
	void login_Success() {
		String rawPassword = "password";
		String encodedPassword = new BCryptPasswordEncoder(10).encode(rawPassword);

		User user = User.builder()
				.id(UUID.randomUUID())
				.email("test@gmail.com")
				.password(encodedPassword)
				.name("Test User")
				.status(UserStatus.builder().name("ACTIVE").build())
				.roles(new HashSet<>())
				.build();

		when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

		AuthenticationRequest request = AuthenticationRequest.builder()
				.email("test@gmail.com")
				.password(rawPassword)
				.build();

		AuthenticationResponse response = authenticationService.login(request);

		assertNotNull(response);
		assertNotNull(response.getToken());
	}

	@Test
	void login_Fail_UserNotFound() {
		when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());

		AuthenticationRequest request = AuthenticationRequest.builder()
				.email("test@gmail.com")
				.password("password")
				.build();

		assertThrows(AppException.class, () -> authenticationService.login(request));
	}

	@Test
	void introspect_InvalidToken() {
		IntrospectRequest request = IntrospectRequest.builder().token("invalid-token-value").build();

		IntrospectResponse response = authenticationService.introspect(request);

		assertNotNull(response);
		assertFalse(response.isValid());
	}
}
