package com.quangtruong.library_management.service.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.quangtruong.library_management.dto.authenticate.AuthenticationRequest;
import com.quangtruong.library_management.dto.authenticate.AuthenticationResponse;
import com.quangtruong.library_management.dto.introspect.IntrospectRequest;
import com.quangtruong.library_management.dto.introspect.IntrospectResponse;
import com.quangtruong.library_management.entity.User;
import com.quangtruong.library_management.exception.AppException;
import com.quangtruong.library_management.exception.ErrorCode;
import com.quangtruong.library_management.repository.IUserRepository;
import com.quangtruong.library_management.service.IAuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService implements IAuthenticationService {

	@Value("${jwt.signerKey}")
	@NonFinal
	String SIGNER_KEY;

	IUserRepository userRepository;

	@Override
	public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
		User user = userRepository.findByEmail(authenticationRequest.getEmail())
				.orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

		if (!passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
			throw new AppException(ErrorCode.UNAUTHORIZED);
		}

		return AuthenticationResponse.builder()
				.token(generateToken(user))
				.build();
	}

	@Override
	public IntrospectResponse introspect(IntrospectRequest request) {
		String token = request.getToken();
		boolean isValid = true;

		try {
			isValid = verifyJWT(token);
		} catch (Exception e) {
			// If there is a parsing or verification error, consider the token invalid
			isValid = false;
		}

		return IntrospectResponse.builder()
				.isValid(isValid)
				.build();
	}

	// Method verifyJWT checks the validity of JWT token and authenticates it
	public boolean verifyJWT(String token) throws JOSEException, ParseException {
		JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

		SignedJWT signedJWT = SignedJWT.parse(token);

		Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

		var verified = signedJWT.verify(verifier);

		return verified && expiryTime.after(new Date());
	}

	// Method generateToken creates a JWT token with user details
	private String generateToken(User user) {
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

		JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
				.subject(user.getId().toString())
				.issuer("sqc.com")
				.issueTime(new Date())
				.expirationTime(new Date(
						Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
				.claim("scope", getRole(user))
				.claim("name", user.getName())
				.build();

		// Create payload from created claims, converting claims object to JSON format
		Payload payload = new Payload(jwtClaimsSet.toJSONObject());

		// Create JWSObject from header and payload, combining them into JWS object
		JWSObject jwsObject = new JWSObject(header, payload);

		try {
			jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));

			return jwsObject.serialize();
		} catch (JOSEException e) {
			throw new RuntimeException(e);
		}
	}

	private String getRole(User user) {
		StringJoiner stringJoiner = new StringJoiner(" ");

		// user.getRoles().forEach(role -> stringJoiner.add(role.getName()););

		user.getRoles().forEach(role -> {stringJoiner.add("ROLE_" + role.getName());

			role.getPermissions()
					.forEach(permission -> {stringJoiner.add(permission.getName());});
		});

		return stringJoiner.toString();
	}
}
