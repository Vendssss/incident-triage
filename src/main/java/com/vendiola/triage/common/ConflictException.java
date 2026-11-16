package com.vendiola.triage.common;

/**
 * Thrown when a request conflicts with existing data, such as a duplicate username. Mapped to HTTP 409.
 */
public class ConflictException extends RuntimeException {

	public ConflictException(String message) {
		super(message);
	}

}
