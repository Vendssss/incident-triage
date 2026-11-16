package com.vendiola.triage.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(@NotBlank @Size(max = 50) String username, @NotBlank @Size(max = 72) String password) {

	@Override
	public String toString() {
		// Never let the password reach logs
		return "LoginRequest[username=" + username + ", password=****]";
	}

}
