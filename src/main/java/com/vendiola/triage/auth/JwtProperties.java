package com.vendiola.triage.auth;

import java.time.Duration;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * JWT settings bound from {@code triage.jwt.*}.
 *
 * @param secret Base64-encoded HMAC key, at least 256 bits
 * @param issuer value of the {@code iss} claim, checked when tokens are decoded
 * @param ttl how long an issued token stays valid
 */
@Validated
@ConfigurationProperties(prefix = "triage.jwt")
public record JwtProperties(@NotBlank String secret, @NotBlank String issuer, @NotNull Duration ttl) {

	private static final int MIN_KEY_BYTES = 32;

	public SecretKey signingKey() {
		byte[] keyBytes;
		try {
			keyBytes = Base64.getDecoder().decode(secret);
		}
		catch (IllegalArgumentException ex) {
			throw new IllegalStateException("triage.jwt.secret must be Base64-encoded", ex);
		}
		if (keyBytes.length < MIN_KEY_BYTES) {
			throw new IllegalStateException(
					"triage.jwt.secret must decode to at least " + MIN_KEY_BYTES + " bytes for HS256");
		}
		return new SecretKeySpec(keyBytes, "HmacSHA256");
	}

	@Override
	public String toString() {
		return "JwtProperties[secret=****, issuer=" + issuer + ", ttl=" + ttl + "]";
	}

}
