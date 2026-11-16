package com.vendiola.triage.user;

import java.time.Instant;

/**
 * Public view of a user. Deliberately excludes the password hash.
 */
public record UserResponse(Long id, String username, String email, Role role, boolean enabled, Instant createdAt) {

	static UserResponse from(User user) {
		return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.isEnabled(),
				user.getCreatedAt());
	}

}
