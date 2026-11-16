package com.vendiola.triage.user;

/**
 * Access level of a user. Stored by name in {@code users.role}.
 */
public enum Role {

	/** Investigates incidents and records findings. */
	ANALYST,

	/** Triages incoming incidents and assigns them to analysts. */
	LEAD,

	/** Manages user accounts and system configuration. */
	ADMIN

}
