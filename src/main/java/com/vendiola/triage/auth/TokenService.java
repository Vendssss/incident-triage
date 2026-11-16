package com.vendiola.triage.auth;

import java.time.Instant;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

/**
 * Issues signed access tokens for authenticated users.
 */
@Service
public class TokenService {

	private static final String ROLE_PREFIX = "ROLE_";

	private final JwtEncoder jwtEncoder;

	private final JwtProperties properties;

	public TokenService(JwtEncoder jwtEncoder, JwtProperties properties) {
		this.jwtEncoder = jwtEncoder;
		this.properties = properties;
	}

	public TokenResponse issue(Authentication authentication) {
		Instant now = Instant.now();
		List<String> roles = authentication.getAuthorities()
			.stream()
			.map(GrantedAuthority::getAuthority)
			.filter(authority -> authority.startsWith(ROLE_PREFIX))
			.map(authority -> authority.substring(ROLE_PREFIX.length()))
			.toList();

		JwtClaimsSet claims = JwtClaimsSet.builder()
			.issuer(properties.issuer())
			.subject(authentication.getName())
			.issuedAt(now)
			.expiresAt(now.plus(properties.ttl()))
			.claim(SecurityConfig.ROLES_CLAIM, roles)
			.build();
		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

		String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
		return new TokenResponse(token, "Bearer", properties.ttl().toSeconds());
	}

}
