package com.vendiola.triage.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Turns exceptions into RFC 9457 problem responses ({@code application/problem+json}).
 * Standard Spring MVC errors (validation, malformed JSON) are handled by the superclass.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(AuthenticationException.class)
	ProblemDetail handleAuthenticationFailure(AuthenticationException ex) {
		// Same message for unknown user, wrong password and disabled account: no account enumeration
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED,
				"Invalid username or password");
		problem.setTitle("Authentication failed");
		return problem;
	}

}
