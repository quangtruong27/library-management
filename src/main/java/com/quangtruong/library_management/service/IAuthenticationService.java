package com.quangtruong.library_management.service;

import com.quangtruong.library_management.dto.authenticate.AuthenticationRequest;
import com.quangtruong.library_management.dto.authenticate.AuthenticationResponse;
import com.quangtruong.library_management.dto.introspect.IntrospectRequest;
import com.quangtruong.library_management.dto.introspect.IntrospectResponse;

public interface IAuthenticationService {
	AuthenticationResponse login(AuthenticationRequest authenticationRequest);

	IntrospectResponse introspect(IntrospectRequest request);
}
