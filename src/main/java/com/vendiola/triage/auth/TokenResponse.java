package com.vendiola.triage.auth;

/**
 * Body returned by a successful login.
 *
 * @param accessToken signed JWT to send as {@code Authorization: Bearer <token>}
 * @param tokenType always {@code Bearer}
 * @param expiresIn token lifetime in seconds
 */
public record TokenResponse(String accessToken, String tokenType, long expiresIn) {
}
