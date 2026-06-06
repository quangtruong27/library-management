package com.quangtruong.library_management.controller;

import com.quangtruong.library_management.dto.ApiResponse;
import com.quangtruong.library_management.dto.authenticate.AuthenticationRequest;
import com.quangtruong.library_management.dto.authenticate.AuthenticationResponse;
import com.quangtruong.library_management.dto.introspect.IntrospectRequest;
import com.quangtruong.library_management.dto.introspect.IntrospectResponse;
import com.quangtruong.library_management.service.IAuthenticationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
	IAuthenticationService authenticationService;

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthenticationResponse>> login(
			@Valid @RequestBody AuthenticationRequest authenticationRequest) {

		return ResponseEntity.status(HttpStatus.OK).body(
				ApiResponse.<AuthenticationResponse>builder()
						.data(authenticationService.login(authenticationRequest))
						.build()
		);
	}

	@PostMapping("/introspect")
	public ResponseEntity<ApiResponse<IntrospectResponse>> introspect(
			@RequestBody IntrospectRequest request) {

		var result = authenticationService.introspect(request);
		
		return ResponseEntity.status(HttpStatus.OK).body(
				ApiResponse.<IntrospectResponse>builder()
						.data(result)
						.build()
		);
	}
}
