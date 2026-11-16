package com.vendiola.triage.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Body for creating a user. The password limit is 72 characters because BCrypt ignores anything longer.
 */
public record CreateUserRequest(
		@NotBlank @Size(min = 3, max = 50)
		@Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "may contain only letters, digits, '.', '_' and '-'")
		String username,

		@NotBlank @Email @Size(max = 255) String email,

		@NotBlank @Size(min = 12, max = 72) String password,

		@NotNull Role role) {

	@Override
	public String toString() {
		return "CreateUserRequest[username=" + username + ", email=" + email + ", password=****, role=" + role
				+ "]";
	}

}
